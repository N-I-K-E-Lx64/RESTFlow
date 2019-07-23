package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public class CStringVariable implements IVariable {

    private final String mName;
    private String mValue;


    public CStringVariable(@NonNull final String mName) {
        this.mName = mName;
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
}
