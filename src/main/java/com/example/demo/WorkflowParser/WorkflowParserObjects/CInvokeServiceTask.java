package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.util.Map;

public class CInvokeServiceTask implements ITask {
    private String mTitle;
    private int mResourceIndex;
    private Map<String, IParameter> mInput;
    private final Api mApi;
    private final EWorkflowTaskType mTaskType;
    private boolean mIsValidatorRequired;
    private ITaskAction mAssignTask;

    public CInvokeServiceTask(String pTitle, int pMethodIndex, Api pApi) {
        this.mTitle = pTitle;
        this.mResourceIndex = pMethodIndex;
        this.mApi = pApi;
        this.mTaskType = EWorkflowTaskType.INVOKESERVICE;
        this.mIsValidatorRequired = false;
    }

    public CInvokeServiceTask setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
        return this;
    }

    public CInvokeServiceTask setValidator(boolean pIsValidatorRequired) {
        this.mIsValidatorRequired = pIsValidatorRequired;
        return this;
    }

    @Override
    public Object get() {
        return this;
    }

    @NonNull
    @Override
    public EWorkflowTaskType getWorkflowType() {
        return mTaskType;
    }

    @NonNull
    public Map<String, IParameter> parameters() {
        return mInput;
    }

    public int resourceIndex() {
        return mResourceIndex;
    }

    public Api api() {
        return mApi;
    }

    public boolean isValidatorRequired() {
        return mIsValidatorRequired;
    }
}