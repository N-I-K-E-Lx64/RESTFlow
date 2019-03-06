package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

public class CVariable implements IVariable {

    private final String mName;
    private JsonNode mValue;

    public CVariable(@NonNull String pName) {
        this.mName = pName;
    }

    @Override
    public void setValue(@NonNull JsonNode pValue) {
        mValue = pValue;
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
