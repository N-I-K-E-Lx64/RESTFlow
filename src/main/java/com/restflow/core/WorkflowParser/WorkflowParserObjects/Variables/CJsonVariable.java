package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.WorkflowParser.CWorkflowParseException;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;

public class CJsonVariable extends AVariable {

    private JsonNode mValue;
    private final ObjectMapper mapper = new ObjectMapper();

    public CJsonVariable(@NonNull final String name) {
        super(name, EVariableType.JSON);
    }

    @Override
    public void setValue(String value) {
        try {
            this.mValue = mapper.readTree(value);
        } catch (JsonProcessingException e) {
            throw new CWorkflowParseException(MessageFormat.format("Could not convert string [{0}] to JsonNode", value));
        }
    }

    @Override
    public JsonNode value() {
        return mValue;
    }
}
