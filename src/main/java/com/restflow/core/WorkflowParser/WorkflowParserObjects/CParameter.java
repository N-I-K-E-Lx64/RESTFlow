package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public class CParameter<T> implements IParameter {

    private T mValue;
    private String mParameterName;
    private Boolean mIsUserParameter;

    public CParameter(T pValue, String pParameterName, Boolean pIsUserParameter) {
        this.mValue = pValue;
        this.mParameterName = pParameterName;
        this.mIsUserParameter = pIsUserParameter;
    }

    public CParameter(String pParameterName, boolean pIsUserParameter) {
        this.mParameterName = pParameterName;
        this.mIsUserParameter = pIsUserParameter;
    }

    @NonNull
    @Override
    public String name() {
        return mParameterName;
    }

    public T value() {
        return mValue;
    }

    public Boolean isUserParameter() {
        return mIsUserParameter;
    }

    public IParameter setValue(Object pValue) {
        mValue = (T) pValue;

        return this;
    }
}
