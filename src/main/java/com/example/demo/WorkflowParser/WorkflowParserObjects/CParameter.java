package com.example.demo.WorkflowParser.WorkflowParserObjects;

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

    public void setValue(T pValue) {
        this.mValue = pValue;
    }

    @NonNull
    @Override
    public String name() {
        return mName;
    }

    @NonNull
    @Override
    public Object value() {
        return mValue;
    }
}
