package com.example.demo.WorkflowParser.WorkflowObjects;

import java.util.Map;

public class CInvokeServiceDefinition implements ITask {

    private final String mTitle;
    private final int mMethodIndex;
    //TODO : Parameter überdenken!
    private Map<String, CParameter> mInput;
    private Map<String, CParameter> mUserInput;

    public CInvokeServiceDefinition(String pTitle, int pMethodIndex, Map<String, CParameter> pInput, Map<String, CParameter> pUserInput) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
        this.mInput = pInput;
        this.mUserInput = pUserInput;
    }

    @Override
    public CInvokeServiceDefinition get() {
        return this;
    }
}
