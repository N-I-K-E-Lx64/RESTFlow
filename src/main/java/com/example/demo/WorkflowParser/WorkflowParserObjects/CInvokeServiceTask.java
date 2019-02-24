package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.util.Map;

public class CInvokeServiceTask implements ITask {

    private String mTitle;
    private int mResourceIndex;
    private Map<String, IParameter> mInput;
    private final Api mApi;
    private boolean mIsValidatorRequired;
    private final EWorkflowTaskType mTaskType;
    private CAssignTask mAssignTask;

    public CInvokeServiceTask(String pTitle, int pMethodIndex, Api pApi) {
        this.mTitle = pTitle;
        this.mResourceIndex = pMethodIndex;
        this.mApi = pApi;
        this.mTaskType = EWorkflowTaskType.INVOKESERVICE;
        this.mIsValidatorRequired = false;
    }

    public void setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
    }

    public void setValidator(boolean pIsValidatorRequired) {
        this.mIsValidatorRequired = pIsValidatorRequired;
    }

    public void setAssignTask(CAssignTask pAssignTask) {
        this.mAssignTask = pAssignTask;
    }

    @NonNull
    @Override
    public Object raw() {
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

    public CAssignTask assignTask() {
        return mAssignTask;
    }
}