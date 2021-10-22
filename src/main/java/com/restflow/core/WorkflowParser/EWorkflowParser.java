package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowDatabase.EWorkflowDefinitions;
import com.restflow.core.WorkflowExecution.Condition.EConditionType;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.*;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.*;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CIntegerVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CJsonVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CStringVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CVariableReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

//TODO : Put all Path String Values in constants!
public enum EWorkflowParser {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowParser.class);

    private static final String VARIABLE_PREFIX = "VARIABLES";
    private static final String HTTP_PREFIX = "http://";

    private StorageService storageService;

    private String tempWorkflowName;

    private ObjectMapper mapper = new ObjectMapper();

    //TODO : Dirty Workaround
    public void init(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Wandelt das Modell eines Workflows in ein ausführbares Workflow Objekt um!
     *
     * @return Vollständiges IWorkflow-Objekt
     * @throws IOException if ResourceFile can not be opened or found
     *
     * @see CWorkflow
     */
    public IWorkflow parseWorkflow(Resource workflowResource) throws IOException {

        File workflowJsonFile = workflowResource.getFile();

        //Workflow Node works as the rootNode
        JsonNode rootNode = mapper.readTree(workflowJsonFile);

        logger.info("Successfully load WorkflowFile!");

        JsonNode workflowNode = rootNode.path("workflow");
        String lWorkflowModel = workflowNode.path("name").asText();
        String lWorkflowDescription = workflowNode.path("description").asText();

        tempWorkflowName = lWorkflowModel;

        Map<String, IVariable> lVariables = new LinkedHashMap<>();

        if (workflowNode.has("variables")) {
            for (Iterator<JsonNode> variableIterator = workflowNode.path("variables").elements(); variableIterator.hasNext(); ) {
                IVariable lTempVariable = createVariable(variableIterator.next());
                lVariables.put(lTempVariable.name(), lTempVariable);
            }
        }

        CWorkflow lWorkflow = new CWorkflow(lWorkflowModel, lWorkflowDescription, lVariables);
        EVariableTempStorage.INSTANCE.setReference(lVariables);

        JsonNode processNode = workflowNode.path("process");
        Queue<ITask> lTasks = parseProcessNode(processNode);
        lWorkflow.generateExecutionOrder(lTasks);

        EWorkflowDefinitions.INSTANCE.addExecutionOrder(lTasks, lWorkflowModel);

        logger.info("Successfully parsed Workflow-Model: " + lWorkflowModel);

        return lWorkflow;
    }

    /**
     *
     * @param processNode JsonNode mit einem Array an WorkflowTasks. Die einzelnen Tasks werden in der Reihenfolge
     *                    ihres Arrays abgearbeitet, somit wird diese Reihenfolge ebenfalls in die ExecutionOrder
     *                    übertragen!
     * @return Queue von ITask-Objekten
     */
    public Queue<ITask> parseProcessNode(JsonNode processNode) {

        Queue<ITask> lExecutionOrder = new ConcurrentLinkedQueue<>();

        Consumer<JsonNode> test = jsonNode -> {
            String lTaskType = jsonNode.path("type").asText();
            JsonNode lTaskDataNode = jsonNode.path("data");

            logger.info(MessageFormat.format("Parsing a [{0}] Task", lTaskType));

            switch (lTaskType.toUpperCase()) {
                case "INVOKE":
                    lExecutionOrder.add(parseInvokeNode(lTaskDataNode));
                    break;

                case "SWITCH":
                    lExecutionOrder.add(parseSwitchNode(lTaskDataNode));
                    break;

                case "ASSIGN":
                    lExecutionOrder.add(parseAssignNode(lTaskDataNode));
                    break;

                case "SEND":
                    lExecutionOrder.add(parseSendNode(lTaskDataNode));
                    break;

                case "RECEIVE":
                    lExecutionOrder.add(parseReceiveNode(lTaskDataNode));
                    break;

                default:
                    throw new CWorkflowParseException("Unknown Task Type: " + lTaskType);
            }
        };

        if (processNode.isArray()) {
            processNode.elements().forEachRemaining(test);
        } else {
            throw new CWorkflowParseException("Workflow Tasks should always be provided in the process array!");
        }

        return lExecutionOrder;
    }

    /**
     * Methode zum parsen eines abstrakten Invoke Task Modells
     *
     * @param invokeNode JsonNode mit dem Modell eines Invoke Tasks
     * @return ITask-Objekt (Invoke Task)
     *
     * @see CInvokeServiceTask
     */
    public ITask parseInvokeNode(JsonNode invokeNode) {

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

        if (invokeNode.has("assignTo")) {
            invokeServiceBuilder.setAssignTask(buildAssignTask(invokeNode.path("assignTo").asText()));
        }

        return invokeServiceBuilder;
    }

    /**
     * Methode zum parsen eines abstrakten Switch Task Modells
     *
     * @param switchNode JsonNode mit dem Modell eines Switch Tasks
     * @return ITask Objekt (Switch Task)
     *
     * @see CSwitchTask
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
     * Methode zum parsen eines abstrakten Assign Task Modells
     *
     * @param assignNode JsonNode mit dem Modell eines Assign Tasks
     * @return ITask Objekt (Assign-Task)
     *
     * @see CAssignTask
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

    public ITask parseSendNode(JsonNode sendNode) {

        String lTargetSystemUrl = HTTP_PREFIX + sendNode.path("target").asText();
        String lWorkflowInstance = sendNode.path("workflow").asText();
        IVariable lSourceVariable = EVariableTempStorage.INSTANCE.apply(sendNode.path("variable").asText());
        int lActivityId = sendNode.path("activityId").asInt();

        return new CSendTask(lTargetSystemUrl, lWorkflowInstance, lSourceVariable, lActivityId);
    }

    public ITask parseReceiveNode(JsonNode receiveNode) {

        int lActivityId = receiveNode.path("activityId").asInt();
        IVariable lTargetVariable = EVariableTempStorage.INSTANCE.apply(receiveNode.path("assignTo").asText());

        return new CReceiveTask(lActivityId, lTargetVariable);
    }

    /**
     * Wandelt alle Parameter Modelle im Input Abschnitt eines Invoke Tasks Modells in nutzbare Parameter Objekte um
     *
     * @param inputNode JsonNode mit allen wichtigen Informationen
     * @return Map mit allen Parameter-Objekten
     *
     * @see CParameter
     */
    public Map<String, IParameter<?>> parseInputNode(JsonNode inputNode) {

        Map<String, IParameter<?>> lParameters = new HashMap<>();
        if (inputNode.has("user-parameter")) {
            JsonNode userParameters = inputNode.path("user-parameter");
            if (userParameters.isArray()) {
                for (JsonNode userParameterNodeElement : userParameters) {
                    String lParameterId = userParameterNodeElement.path("name").asText();
                    String lParameterType = userParameterNodeElement.path("type").asText();
                    Class<?> test = EParameterFactory.INSTANCE.determineClass(lParameterType);
                    lParameters.put(lParameterId,
                            EParameterFactory.INSTANCE.createParameter(lParameterId, true, test));
                }
            } else {
                throw new CWorkflowParseException("Invoke-Input-User Parameters should always be provided in an array!");
            }
        }

        // Used for constant Parameters
        if (inputNode.has("parameter")) {
            JsonNode nonUserParameters = inputNode.path("parameter");
            if (nonUserParameters.isArray()) {
                for (JsonNode parameterNode : nonUserParameters) {
                    IParameter lParsedParameter = parseParameterNode(parameterNode);

                    lParameters.put(lParsedParameter.id(), lParsedParameter);
                }
            } else {
                throw new CWorkflowParseException("Invoke-Input-Const Parameters should always be provided in an array!");
            }
        }

        // Used for Variable References
        if (inputNode.has("variables")) {
            Map<String, IVariable> lVariables = EVariableTempStorage.INSTANCE.reference();

            if (inputNode.path("variables").isArray()) {
                inputNode.path("variables").forEach(variable -> {
                    String lVariableReference = variable.asText();

                    lParameters.put(lVariableReference,
                            new CVariableReference(lVariableReference, lVariables.get(lVariableReference)));
                });
            } else {
                throw new CWorkflowParseException("Invoke-Input-Variables should always be provided in an array!");
            }
        }

        return lParameters;
    }

    /**
     * Erstellt aus dem abstrakten Modell einer Condition ein Condition Objekt für die Benutzung innerhalb eines Switch
     * Tasks.
     *
     * @param conditionNode Json Modell einer Condition
     * @return ICondition Objekt
     *
     * @see CCondition
     */
    public ICondition createCondition(JsonNode conditionNode) {

        EConditionType lConditionType = EConditionType.INSTANCE.conditionType(conditionNode.path("operator").asText());

        IParameter lParameter1;
        JsonNode conditionParameter1 = conditionNode.path("value1");

        // Falls eine Vergleichsargument eine Variable enthält
        if (!(conditionParameter1.isObject())) {
            lParameter1 = createSwitchParameterReference(conditionParameter1.asText());
        } else {
            lParameter1 = parseParameterNode(conditionParameter1);
        }

        IParameter lParameter2;
        JsonNode conditionParameter2 = conditionNode.path("value2");

        if (!(conditionNode.path("value2").isObject())) {
            lParameter2 = createSwitchParameterReference(conditionParameter2.asText());
        } else {
            lParameter2 = parseParameterNode(conditionParameter2);
        }

        return new CCondition(lConditionType, lParameter1, lParameter2);
    }

    /**
     * Erstellt aus dem Modell einer Variablen ein nutzbares Variablen Objekt
     *
     * @param variableNode Abstraktes Json Modell einer Variablen
     * @return IVariable Objekt
     *
     * @see CJsonVariable
     * @see CStringVariable
     */
    public IVariable createVariable(JsonNode variableNode) {

        String lVariableName = variableNode.path("name").asText();
        switch (variableNode.path("type").asText().toUpperCase()) {
            case "JSON":
                return new CJsonVariable(lVariableName);

            case "STRING":
                return new CStringVariable(lVariableName);

            case "INTEGER":
                return new CIntegerVariable(lVariableName);

            default:
                throw new CWorkflowParseException(MessageFormat.format("Variable type [{0}] unknown", variableNode.path("type").asText()));
        }
    }

    /**
     * Erstellt einen InvokeAssign Task für die Benutzung innerhalb eines Invoke Tasks
     *
     * @param targetVariable Name der Ziel Variablen
     * @return ITask Objekt (InvokeAssign)
     *
     * @see CInvokeAssignTask
     */
    public CInvokeAssignTask buildAssignTask(String targetVariable) {
        String lPrefix = targetVariable.split("\\.")[0];
        String lVariableName = targetVariable.split("\\.")[1];

        Map<String, IVariable> variables = EVariableTempStorage.INSTANCE.reference();

        if (lPrefix.equals(VARIABLE_PREFIX)) {
            IVariable lVariable = variables.get(lVariableName);

            if (!Objects.isNull(lVariable)) {
                return new CInvokeAssignTask(lVariable);
            } else {
                throw new CWorkflowParseException("Variable could not be found. It was probably not created.");
            }
        } else {
            throw new CWorkflowParseException("Results can only be assigned to variables. " +
                    "Variable References should have the Prefix VARIABLES!");
        }
    }

    /**
     * Erstellt eine Variablenreferenz für die Benutzung innerhalb eines Switch Tasks
     *
     * @param pVariableReference VARIABLES Prefix + Name der Variable
     * @return Variablen Referenz
     * @see CVariableReference
     */
    public IParameter createSwitchParameterReference(String pVariableReference) {

        String lPrefix = pVariableReference.split("\\.")[0];
        String lVariableName = pVariableReference.split("\\.")[1];

        if (lPrefix.equals(VARIABLE_PREFIX)) {
            return new CVariableReference(lVariableName, EVariableTempStorage.INSTANCE.apply(lVariableName));
        } else {
            throw new CWorkflowParseException("Variable References should have the Prefix VARIABLES!");
        }
    }

    /**
     * Wandelt das abstrakte Modell eienes konstanten Parameters in ein nutzbares Parameter Objekt um
     *
     * @param parameterNode Modell eines konstanten Parameters
     * @return IParameter Objekt
     * @see CParameter
     */
    public IParameter parseParameterNode(JsonNode parameterNode) {
        String lParameterName = parameterNode.path("name").asText();
        String lParameterType = parameterNode.path("type").asText();
        String lParameterValue = parameterNode.path("value").asText();

        return EParameterFactory.INSTANCE.createParameterWithValue(lParameterName, lParameterType, lParameterValue);
    }
}
