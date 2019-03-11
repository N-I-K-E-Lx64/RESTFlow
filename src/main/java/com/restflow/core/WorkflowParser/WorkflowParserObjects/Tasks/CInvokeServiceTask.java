package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;

public class CInvokeServiceTask implements ITask {

    private final String mTitle;
    private final EWorkflowTaskType mTaskType;
    private final int mResourceIndex;
    private Map<String, IParameter> mInput;
    private final Api mApi;

    private CInvokeAssignTask mAssignTask;

    public CInvokeServiceTask(String pTitle, int pMethodIndex, Api pApi) {
        this.mTitle = MessageFormat.format("Invoking Webservice {0}", pTitle);
        this.mResourceIndex = pMethodIndex;
        this.mApi = pApi;
        this.mTaskType = EWorkflowTaskType.INVOKESERVICE;
    }

    public void setInput(Map<String, IParameter> pInput) {
        this.mInput = pInput;
    }

    public void setAssignTask(CInvokeAssignTask pAssignTask) {
        this.mAssignTask = pAssignTask;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public String title() {
        return mTitle;
    }

    @NonNull
    @Override
    public EWorkflowTaskType taskType() {
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

    public CInvokeAssignTask assignTask() {
        return mAssignTask;
    }

    public void resetInput() {
        mInput.forEach((key, value) -> value.setValue(null));
    }
}