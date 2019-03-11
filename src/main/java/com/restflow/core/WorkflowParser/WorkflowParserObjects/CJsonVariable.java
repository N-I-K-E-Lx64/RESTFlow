package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

public class CJsonVariable implements IVariable {

    private final String mName;
    private JsonNode mValue;

    public CJsonVariable(@NonNull final String pName) {
        this.mName = pName;
    }

    @Override
    public void setValue(@NonNull final Object pValue) {
        mValue = (JsonNode) pValue;
    }

    @NonNull
    @Override
    public JsonNode value() {
        return mValue;
    }

    @NonNull
    @Override
    public String name() {
        return mName;
    }
}
