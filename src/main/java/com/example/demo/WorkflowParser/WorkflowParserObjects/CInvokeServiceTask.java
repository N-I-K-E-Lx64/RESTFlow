package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.raml.v2.api.model.v10.api.Api;

import java.util.Map;

public class CInvokeServiceTask implements ITask {
    private String mTitle;
    private int mMethodIndex;
    private Map<String, IParameter> mInput;
    private final Api mApi;
    private final EWorkflowTaskType mTaskType;
    private CAssignTask mAssignTask;

    public CInvokeServiceTask(String pTitle, int pMethodIndex, Api pApi) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
        this.mApi = pApi;
        this.mTaskType = EWorkflowTaskType.INVOKESERVICE;
    }

    public CInvokeServiceTask setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceTask setAssignTask(CAssignTask pAssignTask) {
        this.mAssignTask = pAssignTask;
        return this;
    }

    @Override
    public Object get() {
        return this;
    }

    @Override
    public EWorkflowTaskType getWorkflowType() {
        return mTaskType;
    }
}