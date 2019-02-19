package com.example.demo.WorkflowParser;

import com.example.demo.Storage.StorageService;
import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.example.demo.WorkflowParser.WorkflowParserObjects.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
                IVariable lTempVariable = new CVariable(variableIterator.next().asText());
                lVariables.put(lTempVariable.name(), lTempVariable);
            }
        }

        CWorkflow lWorkflow = new CWorkflow(lWorkflowTitle, lWorkflowDescription, lVariables);
        CVariableTempStorage.getInstance().setReference(lVariables);

        JsonNode processNode = workflowNode.path("process");
        lWorkflow.generateExecutionOrder(parseProcessNode(processNode));

        logger.info("Successfully parsed Workflow: " + lWorkflowTitle);

        this.workflowTitle = null;

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
        //TODO: Get RAML-File from StorageService

        final Api lApi;
        try {
            lApi = ERamlParser.INSTANCE.parseRaml(
                    this.storageService.loadAsResource(invokeNode.path("raml").asText(), this.workflowTitle));
        } catch (IOException e) {
            throw new WorkflowParseException("Cannot parse RAML-File", e);
        }

        int lResourceIndex = IntStream.range(0, lApi.resources().size())
                .filter(resourceIndex -> lApi.resources().get(resourceIndex).relativeUri().value().equals(invokeNode.path("resource").asText()))
                .findFirst()
                .orElse(-1);

        CInvokeServiceBuilder invokeServiceBuilder = new CInvokeServiceBuilder(lTitle, lResourceIndex,
                EWorkflowTaskType.INVOKESERVICE);

        if (invokeNode.has("input")) {
            invokeServiceBuilder = invokeServiceBuilder.setInput(parseInputNode(invokeNode.path("input")));
        }

        if (invokeNode.has("assignTo")) {
            buildAssignTask(invokeNode.path("assignTo").asText());
        }

        return invokeServiceBuilder.build();
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

    public void buildAssignTask(String assignTo) {
        String lPrefix = assignTo.split("\\.")[0];
        String lVariable = assignTo.split("\\.")[1];

        if (lPrefix.equals("VARIABLES")) {

        }

    }

    public IParameter createVariableReference(String variableName, IVariable variable) {
        return new CVariableReference(variableName, variable);
    }
}