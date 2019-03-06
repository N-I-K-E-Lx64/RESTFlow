package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

public interface IVariable {

    void setValue(JsonNode pValue);

    @NonNull
    JsonNode value();

    @NonNull
    String name();
}
