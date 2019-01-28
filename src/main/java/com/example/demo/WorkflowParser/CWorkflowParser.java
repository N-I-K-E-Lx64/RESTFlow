package com.example.demo.WorkflowParser;

import com.example.demo.WorkflowParser.WorkflowObjects.CInvokeServiceTaskBuilder;
import com.example.demo.WorkflowParser.WorkflowObjects.CParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum CWorkflowParser {

    INSTANCE;

    private Logger logger = LogManager.getLogger(CWorkflowParser.class);

    //TODO : create a input parameter for json File!

    /**
     * Parse the workflow.json File into the required Java Objects
     *
     * @throws IOException if ResourceFile can not be opened or found.
     */
    public void parseWorkflow() throws IOException {
        File workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow1.json");

        logger.info("Successfully load WorkflowFile!");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode workflowNode = objectMapper.readTree(workflowJsonFile);

        String lWorkflowTitle = workflowNode.get("title").asText();
        String lWorkflowDescription = workflowNode.get("description").asText();
        Map<String, String> lVariables = new HashMap<>();

        //TODO : Perhaps it makes more sense to initialize the variables only when they are needed?!
        if (workflowNode.has("variables")) {
            for (Iterator<JsonNode> variableIterator = workflowNode.get("variables").elements(); variableIterator.hasNext(); ) {
                //Initializes the map with null value!
                lVariables.put(variableIterator.next().asText(), null);
            }
        }


        JsonNode sequenceNode = workflowNode.get("sequence");
        if (sequenceNode.isArray()) {
            for (final JsonNode task : sequenceNode) {
                String lTitle = task.get("title").asText();
                //TODO : Change this to API and invoke the parseRamlToApi-method!
                String lRAMLFile = task.get("RAML-File").asText();
                //TODO : Change this to Int and invoke the method which returns the index of these method from the RAML-File.
                String lmethod = task.get("method").asText();

                CInvokeServiceTaskBuilder lInvokeServiceTaskBuilder = new CInvokeServiceTaskBuilder(lTitle, null, 0);

                JsonNode input = task.get("input");

                if (input.has("user_parameter")) {
                    Map<String, CParameter> lUserParameters = new HashMap<>();
                    for (Iterator<JsonNode> userParamsIterator = input.get("user_parameter").elements(); userParamsIterator.hasNext(); ) {
                        logger.info(userParamsIterator.next());
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
