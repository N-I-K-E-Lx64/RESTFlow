package com.example.demo.WorkflowParser.WorkflowObjects;

import org.raml.v2.api.model.v10.api.Api;

import java.util.Map;

public class CInvokeServiceTask implements ITask {

    private final String mTitle;
    private final Api mApi;
    private final int mMethodIndex;
    private Map<String, CParameter> mInput;
    private Map<String, CParameter> mUserInput;

    public CInvokeServiceTask(String pTitle, Api pApi, int pMethodIndex, Map<String, CParameter> pInput, Map<String, CParameter> pUserInput) {
        this.mTitle = pTitle;
        this.mApi = pApi;
        this.mMethodIndex = pMethodIndex;
        this.mInput = pInput;
        this.mUserInput = pUserInput;
    }
}
