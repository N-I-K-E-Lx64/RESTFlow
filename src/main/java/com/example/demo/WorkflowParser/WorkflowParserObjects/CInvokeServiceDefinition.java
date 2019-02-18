package com.example.demo.WorkflowParser.WorkflowParserObjects;

import java.util.Map;

public class CInvokeServiceDefinition implements ITask {

    private final String mTitle;
    private final int mMethodIndex;
    private Map<String, IParameter> mInput;

    public CInvokeServiceDefinition(String pTitle, int pMethodIndex, Map<String, IParameter> pInput) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
        this.mInput = pInput;
    }

    @Override
    public CInvokeServiceDefinition get() {
        return this;
    }
}
