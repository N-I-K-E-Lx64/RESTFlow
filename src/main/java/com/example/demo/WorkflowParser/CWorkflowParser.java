package com.example.demo.WorkflowParser;

import com.example.demo.WorkflowParser.WorkflowObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowObjects.IParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum CWorkflowParser {

    INSTANCE;

    private File workflowJsonFile;

    public void parseWorkflow() throws IOException {
        workflowJsonFile = ResourceUtils.getFile("classpath:SampleRessources/JSON-Files/workflow1.json");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode workflowNode = objectMapper.readTree(workflowJsonFile);
        JsonNode sequenceNode = workflowNode.get("sequence");

        if (sequenceNode.isArray()) {
            for (final JsonNode task : sequenceNode) {
                String title = task.get("title").asText();
                //TODO : Change this to API and invoke the parseRamlToApi-method!
                String RAMLFile = task.get("RAML-File").asText();
                //TODO : Change this to Int and invoke the method which returns the index of these method from the RAML-File.
                String method = task.get("method").asText();

                Stream.of(task.get("input").elements()).forEach(input -> createInput(input.next()));
            }
        }

        String workflowTitle = workflowNode.get("title").asText();
        String workflowDescription = workflowNode.get("description").asText();
        JsonNode variables = workflowNode.get("variables");

        // TODO : Better Solution
        Map<String, String> variableMap = new HashMap<>();
        if (variables.isArray()) {
            for (final JsonNode variable : variables) {
                variableMap.put(variable.toString(), null);
            }
        }

    }

    //TODO : Check for Basic-Parameter (String, Int, Double, etc.)!
    public void createInput(JsonNode input) {
        IParameter parameter = new CParameter<>(input.get("value").asInt());
    }

}
