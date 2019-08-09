package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.fasterxml.jackson.databind.JsonNode;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

public class CJsonVariable implements IVariable {

    private final String mName;
    private JsonNode mValue;

    private final EVariableType mVariableType;

    public CJsonVariable(@NonNull final String pName) {
        this.mName = pName;
        this.mVariableType = EVariableType.JSON;
    }

    @Override
    public void setValue(@NonNull final Object pValue) {
        mValue = (JsonNode) pValue;
    }

    @Override
    public JsonNode value() {
        return mValue;
    }

    @NonNull
    @Override
    public String name() {
        return mName;
    }

    @NonNull
    @Override
    public EVariableType variableType() {
        return mVariableType;
    }
}
