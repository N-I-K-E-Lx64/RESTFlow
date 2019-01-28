package com.example.demo.WorkflowParser.WorkflowObjects;

import org.raml.v2.api.model.v10.api.Api;

import java.util.Map;

public class InvokeServiceTask implements ITask {

    private final String title;
    private final Api api;
    private final int method;
    private Map<String, IParameter> input;
    private Map<String, IParameter> userInput;

    public InvokeServiceTask(String title, Api api, int method, Map<String, IParameter> input, Map<String, IParameter> userInput) {
        this.title = title;
        this.api = api;
        this.method = method;
        this.input = input;
        this.userInput = userInput;
    }
}
