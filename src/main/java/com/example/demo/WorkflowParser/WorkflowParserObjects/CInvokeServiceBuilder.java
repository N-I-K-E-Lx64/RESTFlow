package com.example.demo.WorkflowParser.WorkflowParserObjects;

import java.util.Map;

public class CInvokeServiceBuilder {
    private String mTitle;
    private int mMethodIndex;
    private Map<String, IParameter> mInput;

    public CInvokeServiceBuilder(String pTitle, int pMethodIndex) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
    }

    public CInvokeServiceBuilder setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceDefinition build() {
        return new CInvokeServiceDefinition(mTitle, mMethodIndex, mInput);
    }
}