package com.example.demo.WorkflowParser.WorkflowObjects;

import org.raml.v2.api.model.v10.api.Api;

import java.util.Map;

public class CInvokeServiceTaskBuilder {
    private String pTitle;
    private Api pApi;
    private int pMethodIndex;
    private Map<String, CParameter> pInput;
    private Map<String, CParameter> pUserInput;

    public CInvokeServiceTaskBuilder(String pTitle, Api pApi, int pMethodIndex) {
        this.pTitle = pTitle;
        this.pApi = pApi;
        this.pMethodIndex = pMethodIndex;
    }

    String title() {
        return pTitle;
    }

    Api api() {
        return pApi;
    }

    int methodIndex() {
        return pMethodIndex;
    }

    Map<String, CParameter> input() {
        return pInput;
    }

    Map<String, CParameter> userInput() {
        return pUserInput;
    }

    public CInvokeServiceTaskBuilder setInput(Map<String, CParameter> pInput) {
        this.pInput = pInput;
        return this;
    }

    public CInvokeServiceTaskBuilder setUserInput(Map<String, CParameter> pUserInput) {
        this.pUserInput = pUserInput;
        return this;
    }

    public CInvokeServiceTaskBuilder build() {
        return this;
    }
}