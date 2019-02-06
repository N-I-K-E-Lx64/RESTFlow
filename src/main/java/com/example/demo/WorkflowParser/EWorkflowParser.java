package com.example.demo.WorkflowParser;

import com.example.demo.RamlToApiParser;
import com.example.demo.WorkflowParser.WorkflowObjects.CInvokeServiceDefinitionBuilder;
import com.example.demo.WorkflowParser.WorkflowObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowObjects.CWorkflow;
import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

public enum EWorkflowParser {

    INSTANCE;

    private Logger logger = LogManager.getLogger(EWorkflowParser.class);

    //TODO : create a input parameter for json File!

    /**
     * Parse the workflow.json File into the required Java Objects
     *
     * @throws IOException if ResourceFile can not be opened or found.
     */
    public void parseWorkflow() throws IOException {

        File workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow.json");
        //File workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow1.json");

        logger.info("Successfully load WorkflowFile!");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode workflowNode = objectMapper.readTree(workflowJsonFile);

        String lWorkflowTitle = workflowNode.get("title").asText();
        String lWorkflowDescription = workflowNode.get("description").asText();

        Map<String, JsonNode> lVariables = new HashMap<>();

        //TODO : Perhaps it makes more sense to initialize the variables only when they are needed?!
        if (workflowNode.has("variables")) {
            for (Iterator<JsonNode> variableIterator = workflowNode.get("variables").elements(); variableIterator.hasNext(); ) {
                //Initializes the map with null value!
                lVariables.put(variableIterator.next().asText(), null);
            }
        }

        IWorkflow lWorkflow = new CWorkflow(lWorkflowTitle, lWorkflowDescription);

        /*
         * Parse the Sequence Part.
         */
        JsonNode sequenceNode = workflowNode.get("sequence");
        if (sequenceNode.isArray()) {
            for (final JsonNode task : sequenceNode) {
                String lTitle = task.get("title").asText();
                // Converts Api and saves it into the Storage!
                //TODO : Change this for the File Upload!
                Api l_Api = RamlToApiParser.getInstance().convertRamlToApi(task.get("RAML-File").asText());

                // Returns the index of the resource in the RAML file.
                String lMethod = task.get("resource").asText();
                int lResourceIndex = IntStream.range(0, l_Api.resources().size())
                        .filter(resourceIndex -> l_Api.resources().get(resourceIndex).relativeUri().value().equals(lMethod))
                        .findFirst()
                        .orElse(-1);

                logger.info(lResourceIndex);

                CInvokeServiceDefinitionBuilder lInvokeServiceTaskBuilder = new CInvokeServiceDefinitionBuilder(lTitle, lResourceIndex);

                JsonNode input = task.get("input");

                if (input.has("user_parameter")) {
                    Map<String, CParameter> lUserParameters = new HashMap<>();
                    for (Iterator<JsonNode> userParamsIterator = input.get("user_parameter").elements(); userParamsIterator.hasNext(); ) {
                        logger.info(userParamsIterator.next());
                        //TODO : Create a method which generifies the CParameter!
                        CParameter<?> parameter = new CParameter<>(1);
                    }

                } else if (input.has("parameter")) {
                    Map<String, CParameter> lParameters = new HashMap<>();
                    for (Iterator<JsonNode> paramsIterator = input.get("parameter").elements(); paramsIterator.hasNext(); ) {
                        logger.info(paramsIterator.next());
                    }
                }
            }
        }
    }
}
