package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

public class CIntegerVariable implements IVariable {

    private final String mName;
    private final EVariableType mVariableType;
    private Integer mValue;

    public CIntegerVariable(@NonNull final String mName) {
        this.mName = mName;
        this.mVariableType = EVariableType.INTEGER;
    }

    @Override
    public void setValue(Object pValue) {
        mValue = (Integer) pValue;
    }

    @Override
    public Object value() {
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
