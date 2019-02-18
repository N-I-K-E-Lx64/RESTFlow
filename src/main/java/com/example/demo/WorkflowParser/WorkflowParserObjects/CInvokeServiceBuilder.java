package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;

import java.util.Map;

public class CInvokeServiceBuilder {
    private String mTitle;
    private int mMethodIndex;
    private Map<String, IParameter> mInput;
    private final EWorkflowTaskType mTaskType;

    public CInvokeServiceBuilder(String pTitle, int pMethodIndex, EWorkflowTaskType pTaskType) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
        this.mTaskType = pTaskType;
    }

    public CInvokeServiceBuilder setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceDefinition build() {
        return new CInvokeServiceDefinition(mTitle, mMethodIndex, mTaskType, mInput);
    }
}