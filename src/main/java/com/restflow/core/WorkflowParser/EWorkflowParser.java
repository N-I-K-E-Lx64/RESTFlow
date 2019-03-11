package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.EWorkflowDefinitons;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowExecution.Condition.EConditionType;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.*;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public enum EWorkflowParser {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowParser.class);

    private StorageService storageService;
    private String workflowTitle;

    //TODO : Dirty Workaround
    public void init(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Erstellt ein Workflow-Objekt.
     *
     * @return fertiges IWorkflow-Objekt
     * @throws IOException if ResourceFile can not be opened or found.
     */
    public IWorkflow parseWorkflow(Resource workflowResource) throws IOException {

        File workflowJsonFile = workflowResource.getFile();

        ObjectMapper objectMapper = new ObjectMapper();
        //Workflow Node works as the rootNode
        JsonNode rootNode = objectMapper.readTree(workflowJsonFile);

        logger.info("Successfully load WorkflowFile!");

        JsonNode workflowNode = rootNode.path("workflow");
        String lWorkflowTitle = workflowNode.path("title").asText();
        String lWorkflowDescription = workflowNode.path("description").asText();

        this.workflowTitle = lWorkflowTitle;

        Map<String, IVariable> lVariables = new HashMap<>();

        if (workflowNode.has("variables")) {
            for (Iterator<JsonNode> variableIterator = workflowNode.path("variables").elements(); variableIterator.hasNext(); ) {
                IVariable lTempVariable = createVariable(variableIterator.next());
                lVariables.put(lTempVariable.name(), lTempVariable);
            }
        }

        CWorkflow lWorkflow = new CWorkflow(lWorkflowTitle, lWorkflowDescription, lVariables);
        CVariableTempStorage.getInstance().setReference(lVariables);

        JsonNode processNode = workflowNode.path("process");
        Queue<ITask> lTasks = parseProcessNode(processNode);
        lWorkflow.generateExecutionOrder(lTasks);

        EWorkflowDefinitons.INSTANCE.addExecutionOrder(lTasks, lWorkflowTitle);

        logger.info("Successfully parsed Workflow: " + lWorkflowTitle);

        return lWorkflow;
    }

    /**
     * Erstellt eine Queue von ITask-Objekten
     *
     * @param processNode JsonNode mit allen wichtigen Informationen
     * @return Queue von ITask-Objekten
     */
    public Queue<ITask> parseProcessNode(JsonNode processNode) {

        Queue<ITask> lExecutionOrder = new ConcurrentLinkedQueue<>();

        if (processNode.has("invoke")) {
            if (processNode.path("invoke").isArray()) {
                for (JsonNode invokeArrayElement : processNode.path("invoke")) {
                    lExecutionOrder.add(parseInvokeNode(invokeArrayElement));
                }
            } else {
                lExecutionOrder.add(parseInvokeNode(processNode.path("invoke")));
            }
        }

        if (processNode.has("switch")) {
            lExecutionOrder.add(parseSwitchNode(processNode.path("switch")));
        }

        if (processNode.has("assign")) {
            if (processNode.path("assign").isArray()) {
                processNode.path("assign").forEach(task -> lExecutionOrder.add(parseAssignNode(task)));
            } else {
                lExecutionOrder.add(parseAssignNode(processNode.path("assign")));
            }
        }

        return lExecutionOrder;
    }

    /**
     * Erstellt ein Objekt, welches die Definition für ausführbare ITaskAction-Objekte enthält
     *
     * @param invokeNode JsonNode mit allen wichtigen Informationen
     * @return ITask´-Objekt mit der Definition zur Erstellung von ausführbaren ITaskAction-Objekten
     */
    public ITask parseInvokeNode(JsonNode invokeNode) {

        String lTitle = invokeNode.path("title").asText("No Title");

        final Api lApi;
        try {
            lApi = ERamlParser.INSTANCE.parseRaml(
                    this.storageService.loadAsResource(invokeNode.path("raml").asText(), this.workflowTitle));
        } catch (IOException e) {
            throw new CWorkflowParseException("Cannot parse RAML-File!", e);
        }

        if (Objects.isNull(lApi)) {
            throw new CWorkflowParseException("Cannot parse RAML-File");
        }

        int lResourceIndex = IntStream.range(0, lApi.resources().size())
                .filter(resourceIndex -> lApi.resources().get(resourceIndex).relativeUri().value().equals(invokeNode.path("resource").asText()))
                .findFirst()
                .orElse(-1);

        if (lResourceIndex == -1) {
            throw new CWorkflowParseException("Resource was not found!");
        }

        CInvokeServiceTask invokeServiceBuilder = new CInvokeServiceTask(lTitle, lResourceIndex, lApi);

        if (invokeNode.has("input")) {
            invokeServiceBuilder.setInput(parseInputNode(invokeNode.path("input")));
        }

        if (invokeNode.has("assignTo")) {
            invokeServiceBuilder.setAssignTask(buildAssignTask(invokeNode.path("assignTo").asText()));
        }

        return invokeServiceBuilder;
    }

    /**
     * @param switchNode
     * @return
     */
    public ITask parseSwitchNode(JsonNode switchNode) {

        ICondition lCondition;
        if (switchNode.has("condition")) {
            lCondition = createCondition(switchNode.path("condition"));
        } else {
            throw new CWorkflowParseException("Switch Task has no condition!");
        }

        return new CSwitchTask(lCondition, parseProcessNode(switchNode.path("case")), parseProcessNode(switchNode.path("otherwise")));
    }

    /**
     * @param assignNode
     * @return
     */
    public ITask parseAssignNode(JsonNode assignNode) {

        IParameter lSourceParameter = null;
        if (assignNode.has("source")) {
            JsonNode lSourceNode = assignNode.path("source");
            if (lSourceNode.path("parameter").has("value")) {
                lSourceParameter = createParameterWithValue(lSourceNode.path("parameter"));
            }
        } else {
            throw new CWorkflowParseException("Assign Task has no Source Parameter");
        }

        IVariable lTargetVariable = null;
        if (assignNode.has("target")) {
            lTargetVariable = CVariableTempStorage.getInstance().apply(assignNode.path("target").asText());
        }

        return new CAssignTask(lSourceParameter, lTargetVariable);
    }

    /**
     * Erstellt alle Parameter Objekte anhand der Informationen im Input-Teil!
     *
     * @param inputNode JsonNode mit allen wichtigen Informationen
     * @return Map mit allen Parameter-Objekten
     */
    public Map<String, IParameter> parseInputNode(JsonNode inputNode) {

        Map<String, IParameter> lParameters = new HashMap<>();
        if (inputNode.has("user-parameter")) {
            JsonNode userParameters = inputNode.path("user-parameter");
            if (userParameters.isArray()) {
                for (JsonNode userParameterNodeElement : userParameters) {
                    IParameter lTempParameter = createParameter(userParameterNodeElement, true);
                    lParameters.put(lTempParameter.name(), lTempParameter);
                }
            } else {
                IParameter lTempParameter = createParameter(inputNode.path("user-parameter"), true);
                lParameters.put(lTempParameter.name(), lTempParameter);
            }
        }

        if (inputNode.has("parameter")) {
            JsonNode nonUserParameters = inputNode.path("parameter");
            if (nonUserParameters.isArray()) {
                for (JsonNode parameterNodeElement : nonUserParameters) {
                    IParameter lTempParameter = createParameter(parameterNodeElement, false);
                    lParameters.put(lTempParameter.name(), lTempParameter);
                }
            } else {
                IParameter lTempParameter = createParameter(inputNode.path("parameter"), false);
                lParameters.put(lTempParameter.name(), lTempParameter);
            }
        }

        if (inputNode.has("variables")) {
            JsonNode variables = inputNode.path("variables");

            Map<String, IVariable> lVariables = CVariableTempStorage.getInstance().reference();

            if (variables.isArray()) {
                for (JsonNode variableNodeElement : variables) {
                    String lTempVariableName = variableNodeElement.path("variable").textValue();
                    lParameters.put(lTempVariableName,
                            createVariableReference(lTempVariableName, lVariables.get(lTempVariableName)));
                }
            } else {
                String lTempVariableName = variables.path("variable").textValue();
                lParameters.put(lTempVariableName,
                        createVariableReference(lTempVariableName, lVariables.get(lTempVariableName)));
            }
        }

        return lParameters;
    }

    /**
     * @param conditionNode
     * @return
     */
    public ICondition createCondition(JsonNode conditionNode) {

        EConditionType lConditionType = EConditionType.INSTANCE.conditionType(conditionNode.path("operator").asText());

        IParameter lValue1;
        if (!(conditionNode.path("value1").isObject())) {
            lValue1 = createSwitchParameterReference(conditionNode.path("value1").textValue());
        } else {
            lValue1 = createParameterWithValue(conditionNode.path("value1"));
        }

        IParameter lValue2;
        if (!(conditionNode.path("value2").isObject())) {
            lValue2 = createSwitchParameterReference(conditionNode.path("value1").textValue());
        } else {
            lValue2 = createParameterWithValue(conditionNode.path("value2"));
        }

        return new CCondition(lConditionType, lValue1, lValue2);
    }

    /**
     * Creates a CParameter Generic-Object
     *
     * @param parameterNode   the Json-Node which holds the Data
     * @param isUserParameter
     * @return finished CParameter Object
     */
    public IParameter createParameter(JsonNode parameterNode, boolean isUserParameter) {
        String lParameterType = parameterNode.path("type").asText();
        String lParameterName = parameterNode.path("name").asText();

        return CParameterFactory.getInstance().createParameter(lParameterType, lParameterName, isUserParameter);
    }

    public IParameter createParameterWithValue(JsonNode parameterNode) {
        String lParameterType = parameterNode.path("type").asText();
        String lParameterValue = parameterNode.path("value").asText();

        return new CParameter(CParameterFactory.getInstance().createParameterValue(lParameterType, lParameterValue),
                parameterNode.path("name").asText(), false);
    }

    /**
     * @param variableNode
     * @return
     */
    public IVariable createVariable(JsonNode variableNode) {

        String lVariableName = variableNode.path("name").asText();
        switch (variableNode.path("type").asText().toUpperCase()) {
            case "JSON":
                return new CJsonVariable(lVariableName);
            case "STRING":
                return new CStringVariable(lVariableName);
            default:
                throw new CWorkflowParseException(MessageFormat.format("Variable type [{0}] unknown", variableNode.path("type").asText()));
        }
    }

    /**
     *
     * @param assignTo
     * @return
     */
    public CInvokeAssignTask buildAssignTask(String assignTo) {
        String lPrefix = assignTo.split("\\.")[0];
        String lVariable = assignTo.split("\\.")[1];

        Map<String, IVariable> variables = CVariableTempStorage.getInstance().reference();

        if (lPrefix.equals("VARIABLES")) {
            String variableKey = variables.entrySet().stream()
                    .filter(variable -> variable.getValue().name().equals(lVariable))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("FALSE");

            if (!variableKey.equals("FALSE")) {
                return new CInvokeAssignTask(variables.get(variableKey));
            } else {
                throw new CWorkflowParseException("Variable could not be found. It was probably not created.");
            }
        } else {
            throw new CWorkflowParseException("Results can only be assigned to variables.");
        }
    }

    public IParameter createVariableReference(String variableName, IVariable variable) {
        return new CVariableReference(variableName, variable);
    }

    public IParameter createSwitchParameterReference(String pParameter) {

        if (pParameter.contains("VARIABLES")) {
            String[] segments = pParameter.split(Pattern.quote("."));

            return createVariableReference(segments[1], CVariableTempStorage.getInstance().apply(segments[1]));
        } else {
            throw new CWorkflowParseException("Keyword for Variables not found!");
        }
    }
}
