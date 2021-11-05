package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowDatabase.EWorkflowDefinitions;
import com.restflow.core.WorkflowExecution.Condition.EConditionType;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.*;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CTransferTask;
import org.raml.v2.api.model.v10.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Service
public class WorkflowParserService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowParserService.class);
    // Constants
    private static final String VARIABLE_PREFIX = "VARIABLES";
    private final ObjectMapper mapper = new ObjectMapper();
    // Services
    private final StorageService storageService;
    private final ParameterFactory parameterFactory;
    private final ParserVariableTempStorage variableTempStorage;
    private final ParserTaskDefinitionTempStorage taskDefinitionTempStorage;

    // Used for accessing the storage Service!
    private String tempWorkflowName;

    @Autowired
    public WorkflowParserService(StorageService storageService,
                                 ParameterFactory parameterFactory,
                                 ParserVariableTempStorage variableTempStorage,
                                 ParserTaskDefinitionTempStorage taskDefinitionTempStorage) {
        this.storageService = storageService;
        this.parameterFactory = parameterFactory;
        this.variableTempStorage = variableTempStorage;
        this.taskDefinitionTempStorage = taskDefinitionTempStorage;
    }

    /**
     * Parses the abstract workflow JSON object into an executable workflow
     *
     * @param workflowResource Uploaded workflow model
     * @return IWorkflow object
     * @throws IOException If ResourceFile can not be opened or found
     */
    public IWorkflow parseWorkflow(Resource workflowResource) throws IOException {

        logger.info("Resetting temp storages!");
        taskDefinitionTempStorage.reset();

        Map<String, IVariable<?>> lVariables;
        Map<String, ITask> lTasks;

        File workflowModel = workflowResource.getFile();

        // Read the JSON tree and save it in a JSON node
        JsonNode rootNode = mapper.readTree(workflowModel);
        logger.info("Successfully loaded workflow model!");

        JsonNode workflowNode = rootNode.path("workflow");
        String lModelName = workflowNode.path("name").asText();
        String lDescription = workflowNode.path("description").asText();

        tempWorkflowName = lModelName;

        // Create the basic workflow object
        IWorkflow lParsedWorkflow = new CWorkflow(lModelName, lDescription);

        if (workflowNode.has("variables")) {
            // Parse the variables!
            lVariables = parseVariables(workflowNode.path("variables"));
            // Set the variables in the workflow object
            lParsedWorkflow.setVariables(lVariables);
            // Set the variable reference
            variableTempStorage.setVariableReferences(lVariables);
        }

        if (workflowNode.has("tasks")) {
            // Parse the tasks
            lTasks = parseTasks(workflowNode.path("tasks"));
        } else {
            throw new CWorkflowParseException(
                    MessageFormat.format("Model [{0}] can not be parsed, because no tasks are provided", lModelName));
        }

        if (workflowNode.has("flow")) {
            Queue<ITask> lExecutionOrder = createExecutionOrder(workflowNode.path("flow"), lTasks);
            // Sets the execution order in the workflow object
            lParsedWorkflow.generateExecutionOrder(lExecutionOrder);
            // Saves the Execution order separately
            EWorkflowDefinitions.INSTANCE.addExecutionOrder(lExecutionOrder, lModelName);
        } else {
            throw new CWorkflowParseException(
                    MessageFormat.format("Model [{0}] can not be parsed, because no process flow is provided", lModelName));
        }

        logger.info("Successfully parsed Workflow-Model: " + lModelName);

        return lParsedWorkflow;
    }

    /**
     * Iterates through all workflow tasks and parses them into the correct task definition.
     *
     * @param activitiesNode JSON Array with all task models
     */
    private Map<String, ITask> parseTasks(@NonNull final JsonNode activitiesNode) {
        Map<String, ITask> lTasks = new HashMap<>();

        Consumer<JsonNode> parseActivity = activity -> {
            String lTaskId = activity.path("id").asText();
            String lDescription = activity.path("description").asText();
            ETaskType lTaskType = ETaskType.valueOf(activity.path("type").asText());

            JsonNode params = activity.path("params");

            ITask lTask;

            switch (lTaskType) {
                case INVOKE:
                    lTask = parseInvokeTask(lTaskId, lDescription, params);
                    break;
                case ASSIGN:
                    lTask = parseAssignTask(lTaskId, lDescription, params);
                    break;
                case TRANSFER:
                    lTask = parseTransferTask(lTaskId, lDescription, params);
                    break;
                case SWITCH:
                    lTask = parseSwitchTask(lTaskId, lDescription, params);
                    break;
                default:
                    throw new CWorkflowParseException(MessageFormat.format("Task type [{0}] unknown!", lTaskType));
            }

            lTasks.put(lTaskId, lTask);
            // Store the parsed task definition temporally
            taskDefinitionTempStorage.setTaskDefinition(lTask);
        };

        if (activitiesNode.isArray()) {
            activitiesNode.elements().forEachRemaining(parseActivity);
        } else {
            throw new CWorkflowParseException("Workflow Tasks should always be provided in the tasks array!");
        }

        return lTasks;
    }

    /**
     * This function iterates through the "flow" array and puts the previously parsed ITask objects in a queue based on the ordering of the array.
     *
     * @param processFlow JSON Array with the taskIds
     * @param tasks       Map containing the parsed ITask objects
     * @return Order in which the ITask objects are to be executed at the end. The order is represented by a
     */
    private Queue<ITask> createExecutionOrder(@NonNull final JsonNode processFlow,
                                              @NonNull final Map<String, ITask> tasks) {
        Queue<ITask> lExecutionOrder = new ConcurrentLinkedQueue<>();

        if (processFlow.isArray()) {
            processFlow.forEach(task -> {
                String lTaskId = task.asText();
                lExecutionOrder.add(tasks.get(lTaskId));
            });
        } else {
            throw new CWorkflowParseException("The process flow should always be modeled as an array!");
        }

        return lExecutionOrder;
    }

    /**
     * @param taskId           Task identifier
     * @param description      Task description
     * @param invokeParameters Task parameters (raml-document, resource, invoke-parameters, target-variable)
     * @return A Model of an invoke task
     * @see CInvokeServiceTask
     */
    private ITask parseInvokeTask(@NonNull final String taskId,
                                  @NonNull final String description,
                                  @NonNull final JsonNode invokeParameters) {
        String lRAMLModel = invokeParameters.path("raml").asText();
        String lResource = invokeParameters.path("resource").asText();

        Resource lRAMLModelResource = storageService.loadAsResource(lRAMLModel, tempWorkflowName);

        final Api lApi = ERamlParser.INSTANCE.parseRaml(lRAMLModelResource);

        if (Objects.isNull(lApi)) {
            throw new CWorkflowParseException(
                    MessageFormat.format("RAML file [{0}] could not be parsed", lRAMLModelResource.getFilename()));
        }

        int lResourceIndex = IntStream.range(0, lApi.resources().size())
                .filter(index -> lApi.resources().get(index).relativeUri().value().equals(lResource))
                .findFirst()
                .orElse(-1);

        if (lResourceIndex == -1)
            throw new CWorkflowParseException(MessageFormat.format("Resource [{0}] could not be found", lResource));

        Map<String, IParameter<?>> lParameters = new LinkedHashMap<>();

        // Handles the input parsing
        if (invokeParameters.has("user-params")) {
            Consumer<JsonNode> parseUserParameters = parameterModel -> {
                String lParameterId = parameterModel.path("id").asText();
                String lParameterType = parameterModel.path("type").asText();
                Class<?> lType = parameterFactory.determineClass(lParameterType);

                IParameter<?> lParameter = parameterFactory.createParameter(lParameterId, true, lType);
                lParameters.put(lParameterId, lParameter);
            };
            // Iterate over all user parameters and create a parameter object
            invokeParameters.path("user-params").elements().forEachRemaining(parseUserParameters);
        } else if (invokeParameters.has("variables")) {
            Consumer<JsonNode> parseVariables = variable -> {
                String lVariableId = variable.asText();

                lParameters.put(lVariableId, new CVariableReference(lVariableId, variableTempStorage.apply(lVariableId)));
            };
            // Iterate over all variables and create a CVariableReference object
            invokeParameters.path("params").elements().forEachRemaining(parseVariables);
        } else {
            throw new CWorkflowParseException("An Invoke task should always have some kind of input");
        }

        CInvokeServiceTask invokeTask = new CInvokeServiceTask(taskId, description, lResourceIndex, lApi, lParameters);

        // Handles the assignment of the results to a specified variable!
        if (invokeParameters.has("variable")) {
            String lTargetVariable = invokeParameters.path("variable").asText();
            invokeTask.setTargetVariable(variableTempStorage.apply(lTargetVariable));
        }

        return invokeTask;
    }

    /**
     * @param taskId           Task identifier
     * @param description      Task description
     * @param assignParameters Task parameters (parameter-id, parameter-value, parameter-type, target-variable)
     * @return Model of an assign task
     * @see CAssignTask
     */
    private ITask parseAssignTask(@NonNull final String taskId,
                                  @NonNull final String description,
                                  @NonNull final JsonNode assignParameters) {
        IParameter<?> lParameter = parseParameterNode(assignParameters);

        String lTargetVariable = assignParameters.path("variable").asText();

        IVariable<?> lTarget = variableTempStorage.apply(lTargetVariable);

        return new CAssignTask(taskId, description, lParameter, lTarget);
    }

    /**
     * Parses the model of a transfer task
     *
     * @param taskId             Task identifier
     * @param description        Task description
     * @param transferParameters Task parameters (source-variable, target-variable)
     * @return Transfer task object
     * @see CTransferTask
     */
    private ITask parseTransferTask(@NonNull final String taskId,
                                    @NonNull final String description,
                                    @NonNull final JsonNode transferParameters) {
        String source = transferParameters.path("source").asText();
        String target = transferParameters.path("target").asText();

        IVariable<?> sourceVariable = variableTempStorage.apply(source);
        IVariable<?> targetVariable = variableTempStorage.apply(target);

        return new CTransferTask(taskId, description, sourceVariable, targetVariable);
    }

    /**
     * Parses the model of a switch task
     *
     * @param taskId           Task identifier
     * @param description      Task description
     * @param switchParameters Task parameters (condition-type, condition-parameter1, condition-parameter-2, true-flow, false-flow)
     *                         The true flow represents the flow when the condition is evaluated true and the false flow the opposite.
     * @return Model of a switch task
     * @see CSwitchTask
     */
    private ITask parseSwitchTask(@NonNull final String taskId,
                                  @NonNull final String description,
                                  @NonNull final JsonNode switchParameters) {
        EConditionType lConditionType = EConditionType.INSTANCE.conditionType(switchParameters.path("operator").asText());
        ICondition condition = createCondition(lConditionType, switchParameters.path("param1"), switchParameters.path("param2"));

        Queue<ITask> trueFlow = createSwitchFlow(switchParameters.path("true-flow"));
        Queue<ITask> falseFlow = createSwitchFlow(switchParameters.path("false-flow"));

        return new CSwitchTask(taskId, description, condition, trueFlow, falseFlow);
    }

    /**
     * Creates a variable generic that has exactly the modeled type
     *
     * @param variableNode JSON object of the variable
     * @return IVariable generic
     */
    private Map<String, IVariable<?>> parseVariables(@NonNull final JsonNode variableNode) {
        Map<String, IVariable<?>> lVariables = new LinkedHashMap<>();

        if (variableNode.isArray()) {
            variableNode.elements().forEachRemaining(variable -> {
                String lVariableName = variable.path("name").asText();
                String lVariableType = variable.path("type").asText();
                Class<?> lType = parameterFactory.determineClass(lVariableType);

                lVariables.put(lVariableName, parameterFactory.createVariable(lVariableName, lType));
            });
        } else {
            throw new CWorkflowParseException("Variables should always be declared in an array!");
        }
        return lVariables;
    }

    private ICondition createCondition(@NonNull final EConditionType conditionType,
                                       @NonNull final JsonNode parameter1,
                                       @NonNull final JsonNode parameter2) {
        IParameter<?> param1;
        IParameter<?> param2;

        // Check if the condition parameters are objects (const parameters) or variable references!
        if (parameter1.isObject()) {
            param1 = parseParameterNode(parameter1);
        } else {
            param1 = createVariableReference(parameter1.asText());
        }

        if (parameter2.isObject()) {
            param2 = parseParameterNode(parameter2);
        } else {
            param2 = createVariableReference(parameter2.asText());
        }

        return new CCondition(conditionType, param1, param2);
    }

    private Queue<ITask> createSwitchFlow(@NonNull final JsonNode switchFlow) {
        Queue<ITask> switchExecutionOrder = new ConcurrentLinkedQueue<>();

        if (switchFlow.isArray()) {
            switchFlow.forEach(task -> {
                String taskId = task.asText();
                switchExecutionOrder.add(taskDefinitionTempStorage.apply(taskId));
            });
        } else {
            throw new CWorkflowParseException("The process flow should always be modeled as an array!");
        }

        return switchExecutionOrder;
    }

    /**
     * Parses a parameter note to an IParameter Generic that actually has the modeled type
     *
     * @param parameterNode Parameter-model (id, value, type)
     * @return IParameter Generic
     * @see CParameter
     */
    private IParameter<?> parseParameterNode(@NonNull final JsonNode parameterNode) {
        String lParamId = parameterNode.path("id").asText();
        String lValue = parameterNode.path("value").asText();
        String lParameterType = parameterNode.path("type").asText();
        Class<?> lType = parameterFactory.determineClass(lParameterType);

        return parameterFactory.createConstParameter(lParamId, lType, lValue);
    }

    /**
     * @param variableName Variable id with the prefix "VARIABLES"
     * @return IParameter object that is a reference to a specific variable
     * @see CVariableReference
     */
    private IParameter<?> createVariableReference(@NonNull final String variableName) {
        IVariable<?> lVariable = variableTempStorage.apply(variableName);
        return new CVariableReference(variableName.split("\\.")[1], lVariable);
    }
}