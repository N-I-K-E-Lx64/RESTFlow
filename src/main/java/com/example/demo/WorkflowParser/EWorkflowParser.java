package com.example.demo.WorkflowParser;

import com.example.demo.RamlToApiParser;
import com.example.demo.WorkflowParser.WorkflowParserObjects.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public enum EWorkflowParser {

    INSTANCE;

    private Logger logger = LogManager.getLogger(EWorkflowParser.class);

    //TODO : create a input parameter for json File!
    //TODO : Change all important attributes in json File to CAPS

    /**
     * Parse the workflow.json File into the required Java Objects
     *
     * @throws IOException if ResourceFile can not be opened or found.
     */
    public void parseWorkflow() throws IOException {

        File workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow-optimized.json");
        //File workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow1.json");

        logger.info("Successfully load WorkflowFile!");

        ObjectMapper objectMapper = new ObjectMapper();
        //Workflow Node works as the rootNode
        JsonNode rootNode = objectMapper.readTree(workflowJsonFile);

        JsonNode workflowNode = rootNode.path("workflow");
        String lWorkflowTitle = workflowNode.path("title").asText();
        String lWorkflowDescription = workflowNode.path("description").asText();

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
    }

    /**
     * @param processNode
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

        return lExecutionOrder;
    }

    /**
     * Erstellt ein ausführbares Objekt, welches einen REST-API Aufruf durchführen kann!
     *
     * @param invokeNode JsonNode mit allen wichtigen Informationen
     * @return Ausführbares ITaskAction-Objekt
     */
    public ITask parseInvokeNode(JsonNode invokeNode) {

        String lTitle = invokeNode.path("title").asText("No Title");
        //TODO: Get RAML-File from StorageService
        try {
            File ramlTestFile = ResourceUtils.getFile("classpath:SampleRessources/RAML-Files/Market.raml");
            final Api lApi = RamlToApiParser.getInstance().convertRamlToApi(ramlTestFile);


            int lResourceIndex = IntStream.range(0, lApi.resources().size())
                    .filter(resourceIndex -> lApi.resources().get(resourceIndex).relativeUri().value().equals(invokeNode.path("resource").asText()))
                    .findFirst()
                    .orElse(-1);

            CInvokeServiceTask invokeService = new CInvokeServiceTask(lTitle, lResourceIndex);

            if (invokeNode.has("input")) {
                invokeService.setInput(parseInputNode(invokeNode.path("input")));
            }

            if (invokeNode.has("assignTo")) {
                invokeService.setAssignTask(buildAssignTask(invokeNode.path("assignTo").asText()));
            }

            return invokeService;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ITask parseSwitchNode(JsonNode switchNode) {

        if (switchNode.has("CONDITION")) {

        } else {
            throw new WorkflowParseException("Switch-Activity hat keine Bedingung hinterlegt!");
        }

        return null;
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

    /**
     * @param assignTo
     * @return
     */
    public CAssignTask buildAssignTask(String assignTo) {
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
                return new CAssignTask(variables.get(variableKey));
            } else {
                throw new WorkflowParseException("Variable could not be found. It was probably not created.");
            }
        } else {
            throw new WorkflowParseException("Results can only be assigned to variables.");
        }
    }

    public IParameter createVariableReference(String variableName, IVariable variable) {
        return new CVariableReference(variableName, variable);
    }
}
