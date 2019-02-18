package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

public interface IVariable {

    void setValue(@NonNull JsonNode pValue);

    @NonNull
    JsonNode value();

    @NonNull
    String name();
}
