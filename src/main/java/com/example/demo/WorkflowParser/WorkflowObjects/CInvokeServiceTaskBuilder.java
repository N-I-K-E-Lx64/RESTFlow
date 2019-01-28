package com.example.demo.WorkflowParser.WorkflowObjects;

import org.raml.v2.api.model.v10.api.Api;

import java.util.Map;

public class CInvokeServiceTaskBuilder {
    private String mTitle;
    private Api mApi;
    private int mMethodIndex;
    private Map<String, CParameter> mInput;
    private Map<String, CParameter> mUserInput;

    public CInvokeServiceTaskBuilder(String pTitle, Api pApi, int pMethodIndex) {
        this.mTitle = pTitle;
        this.mApi = pApi;
        this.mMethodIndex = pMethodIndex;
    }

    String title() {
        return mTitle;
    }

    Api api() {
        return mApi;
    }

    int methodIndex() {
        return mMethodIndex;
    }

    Map<String, CParameter> input() {
        return mInput;
    }

    Map<String, CParameter> userInput() {
        return mUserInput;
    }

    public CInvokeServiceTaskBuilder setInput(Map<String, CParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceTaskBuilder setUserInput(Map<String, CParameter> pUserInput) {
        this.mUserInput = pUserInput;
        return this;
    }

    public CInvokeServiceTask build() {
        return new CInvokeServiceTask(mTitle, mApi, mMethodIndex, mInput, mUserInput);
    }
}