package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class CVariableReference implements IParameter {

    private final String mVariableName;
    private AtomicReference<IVariable> mVariable = new AtomicReference<>();

    public CVariableReference(String pVariableName, IVariable pVariable) {
        this.mVariableName = pVariableName;
        this.mVariable.set(pVariable);
    }

    @NonNull
    @Override
    public String name() {
        return mVariableName;
    }

    @NonNull
    @Override
    public IVariable value() {
        return mVariable.get();
    }

    @Override
    public IParameter setValue(Object pValue) {
        mVariable.get().setValue(pValue);

        return this;
    }
}
