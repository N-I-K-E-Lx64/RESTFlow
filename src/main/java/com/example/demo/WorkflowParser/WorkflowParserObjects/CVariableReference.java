package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class CVariableReference implements IParameter {

    private final String mVariableName;
    private AtomicReference<IVariable> mVariable = new AtomicReference<>();

    public CVariableReference(String mVariableName, IVariable pVariable) {
        this.mVariableName = mVariableName;
        this.mVariable.set(pVariable);
    }

    public void setReference(IVariable pVariable) {
        mVariable.set(pVariable);
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
    public void setValue(Object pValue) {
        mVariable.get().setValue((JsonNode) pValue);
    }
}
