package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public class CParameter<T> implements IParameter {

    private T mValue;
    private String mName;
    private boolean mIsUserParameter;

    public CParameter(T pValue, String pName, Boolean pIsUserParameter) {
        this.mValue = pValue;
        this.mName = pName;
        this.mIsUserParameter = pIsUserParameter;
    }

    public CParameter(String pName, boolean pIsUserParameter) {
        this.mName = pName;
        this.mIsUserParameter = pIsUserParameter;
    }

    @NonNull
    @Override
    public String name() {
        return mName;
    }

    public T value() {
        return mValue;
    }

    public void setValue(@NonNull Object pValue) {
        mValue = (T) pValue;
    }

}
