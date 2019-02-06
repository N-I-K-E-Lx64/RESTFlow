package com.example.demo.WorkflowParser.WorkflowObjects;

import java.util.Map;

public class CInvokeServiceDefinitionBuilder {
    private String mTitle;
    private int mMethodIndex;
    private Map<String, CParameter> mInput;
    private Map<String, CParameter> mUserInput;

    public CInvokeServiceDefinitionBuilder(String pTitle, int pMethodIndex) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
    }

    String title() {
        return mTitle;
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

    public CInvokeServiceDefinitionBuilder setInput(Map<String, CParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceDefinitionBuilder setUserInput(Map<String, CParameter> pUserInput) {
        this.mUserInput = pUserInput;
        return this;
    }

    public CInvokeServiceDefinition build() {
        return new CInvokeServiceDefinition(mTitle, mMethodIndex, mInput, mUserInput);
    }
}