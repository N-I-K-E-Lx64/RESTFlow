package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

public class CStringVariable implements IVariable {

    private final String mName;
    private String mValue;

    private final EVariableType mVariableType;

    public CStringVariable(@NonNull final String mName) {
        this.mName = mName;
        this.mVariableType = EVariableType.STRING;
    }

    @Override
    public void setValue(@NonNull final Object pValue) {
        mValue = (String) pValue;
    }

    @Override
    public String value() {
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
