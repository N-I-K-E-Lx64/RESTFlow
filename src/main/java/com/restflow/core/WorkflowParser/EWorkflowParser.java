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
import java.util.stream.IntStream;

//TODO : Fix Javadoc
public enum EWorkflowParser {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowParser.class);

    private static final String VARIABLE_PREFIX = "VARIABLES";

    private StorageService storageService;
    private String tempWorkflowName;
    private ObjectMapper mapper = new ObjectMapper();

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

        //Workflow Node works as the rootNode
        JsonNode rootNode = mapper.readTree(workflowJsonFile);

        logger.info("Successfully load WorkflowFile!");

        JsonNode workflowNode = rootNode.path("workflow");
        String lWorkflowName = workflowNode.path("name").asText();
        tempWorkflowName = lWorkflowName;
        String lWorkflowDescription = workflowNode.path("description").asText();

        Map<String, IVariable> lVariables = new HashMap<>();

        if (workflowNode.has("variables")) {
            for (Iterator<JsonNode> variableIterator = workflowNode.path("variables").elements(); variableIterator.hasNext(); ) {
                IVariable lTempVariable = createVariable(variableIterator.next());
                lVariables.put(lTempVariable.name(), lTempVariable);
            }
        }

        CWorkflow lWorkflow = new CWorkflow(lWorkflowName, lWorkflowDescription, lVariables);
        EVariableTempStorage.INSTANCE.setReference(lVariables);

        JsonNode processNode = workflowNode.path("process");
        Queue<ITask> lTasks = parseProcessNode(processNode);
        lWorkflow.generateExecutionOrder(lTasks);

        EWorkflowDefinitons.INSTANCE.addExecutionOrder(lTasks, lWorkflowName);

        logger.info("Successfully parsed Workflow: " + lWorkflowName);

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

        try {
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
        } catch (IOException e) {
            throw new CWorkflowParseException(e.getMessage());
        }

        return lExecutionOrder;
    }

    /**
     * Erstellt ein Objekt, welches die Definition für ausführbare ITaskAction-Objekte enthält
     *
     * @param invokeNode JsonNode mit allen wichtigen Informationen
     * @return ITask´-Objekt mit der Definition zur Erstellung von ausführbaren ITaskAction-Objekten
     */
    public ITask parseInvokeNode(JsonNode invokeNode) throws IOException {

        final Api lApi;

        lApi = ERamlParser.INSTANCE.parseRaml(
                this.storageService.loadAsResource(invokeNode.path("raml").asText(), tempWorkflowName));

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

        CInvokeServiceTask invokeServiceBuilder = new CInvokeServiceTask(lApi.title().value(), lResourceIndex, lApi);

        if (invokeNode.has("input")) {
            invokeServiceBuilder.setInput(parseInputNode(invokeNode.path("input")));
        }

        //TODO : Better Solution!
        if (invokeNode.has("assignTo")) {
            invokeServiceBuilder.setAssignTask(buildAssignTask(invokeNode.path("assignTo")));
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

        CAssignTask lAssignTask = new CAssignTask();

        if (assignNode.has("source")) {
            JsonNode lSourceNode = assignNode.path("source");
            if (lSourceNode.isObject()) {
                lAssignTask.setSource(parseParameterNode(lSourceNode));
            }
        } else {
            throw new CWorkflowParseException("Assign Task has no Source Parameter");
        }

        if (assignNode.has("target")) {
            lAssignTask.setTarget(EVariableTempStorage.INSTANCE.apply(assignNode.path("target").asText()));
        }

        return lAssignTask;
    }

    /**
     * Erstellt alle Parameter Objekte anhand der Informationen im Input-Teil!
     *
     * @param inputNode JsonNode mit allen wichtigen Informationen
     * @return Map mit allen Parameter-Objekten
     */
    public Map<String, IParameter> parseInputNode(JsonNode inputNode) throws IOException {

        Map<String, IParameter> lParameters = new HashMap<>();
        if (inputNode.has("user-parameter")) {
            JsonNode userParameters = inputNode.path("user-parameter");
            if (userParameters.isArray()) {
                for (JsonNode userParameterNodeElement : userParameters) {
                    String lParameterName = userParameterNodeElement.path("name").asText();
                    String lParameterType = userParameterNodeElement.path("type").asText();
                    lParameters.put(lParameterName,
                            EParameterFactory.INSTANCE.createParameter(lParameterName, lParameterType, true));
                }
            } else {
                throw new CWorkflowParseException("Invoke-Input-User Parameters should always be provided in an array!");
            }
        }

        // Used for constant Parameters
        if (inputNode.has("parameter")) {
            JsonNode nonUserParameters = inputNode.path("parameter");
            if (nonUserParameters.isArray()) {
                for (JsonNode parameterNodeElement : nonUserParameters) {
                    String lParameterName = nonUserParameters.path("name").asText();
                    String lParameterType = nonUserParameters.path("type").asText();

                    lParameters.put(lParameterName,
                            EParameterFactory.INSTANCE.createParameterWithValue(lParameterType, lParameterName, nonUserParameters.path("value")));
                }
            } else {
                throw new CWorkflowParseException("Invoke-Input-Const Parameters should always be provided in an array!");
            }
        }

        if (inputNode.has("variables")) {
            Map<String, IVariable> lVariables = EVariableTempStorage.INSTANCE.reference();

            if (inputNode.path("variables").isArray()) {
                String[] variables = mapper.readValue(inputNode.path("variables").asText(), String[].class);

                for (String variableReference : variables) {
                    lParameters.put(variableReference,
                            new CVariableReference(variableReference, lVariables.get(variableReference)));
                }
            } else {
                throw new CWorkflowParseException("Invoke-Input-Variables should always be provided in an array!");
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

        IParameter lParameter1;
        JsonNode conditionParameter1 = conditionNode.path("value1");

        // Falls eine Vergleichsargument eine Variable enthält
        if (!(conditionParameter1.isObject())) {
            lParameter1 = createSwitchParameterReference(conditionParameter1.asText());
        } else {
            String lParameterName = conditionParameter1.path("name").asText();
            String lParameterType = conditionParameter1.path("type").asText();
            lParameter1 = EParameterFactory.INSTANCE.createParameterWithValue(lParameterName, lParameterType, conditionParameter1.path("value"));
        }

        IParameter lParameter2;
        JsonNode conditionParameter2 = conditionNode.path("value2");

        if (!(conditionNode.path("value2").isObject())) {
            lParameter2 = createSwitchParameterReference(conditionParameter2.asText());
        } else {
            String lParameterName = conditionParameter2.path("name").asText();
            String lParameterType = conditionParameter2.path("type").asText();
            lParameter2 = EParameterFactory.INSTANCE.createParameterWithValue(lParameterName, lParameterType, conditionParameter1.path("value"));
        }

        return new CCondition(lConditionType, lParameter1, lParameter2);
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
     * @param targetVariable
     * @return
     */
    public CInvokeAssignTask buildAssignTask(JsonNode targetVariable) {
        String lTargetVariable = targetVariable.asText();
        String lPrefix = lTargetVariable.split("\\.")[0];
        String lVariable = lTargetVariable.split("\\.")[1];

        Map<String, IVariable> variables = EVariableTempStorage.INSTANCE.reference();

        if (lPrefix.equals(VARIABLE_PREFIX)) {
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
            throw new CWorkflowParseException("Results can only be assigned to variables. " +
                    "Variable References should have the Prefix VARIABLES!");
        }
    }

    public IParameter createSwitchParameterReference(String pVariableReference) {

        String lPrefix = pVariableReference.split("\\.")[0];
        String lVariableName = pVariableReference.split("\\.")[1];

        if (lPrefix.equals(VARIABLE_PREFIX)) {
            return new CVariableReference(lVariableName, EVariableTempStorage.INSTANCE.apply(lVariableName));
        } else {
            throw new CWorkflowParseException("Variable References should have the Prefix VARIABLES!");
        }
    }

    public IParameter parseParameterNode(JsonNode parameterNode) {
        String lParameterName = parameterNode.path("name").asText();
        String lParameterType = parameterNode.path("type").asText();

        return EParameterFactory.INSTANCE.createParameterWithValue(lParameterName, lParameterType, parameterNode.path("value"));
    }
}
