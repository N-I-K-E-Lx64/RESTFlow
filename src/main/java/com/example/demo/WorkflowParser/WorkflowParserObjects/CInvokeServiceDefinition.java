package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

import java.util.Map;

public class CInvokeServiceDefinition implements ITask {

    private final String mTitle;
    private final int mMethodIndex;
    private Map<String, IParameter> mInput;
    private final EWorkflowTaskType mTaskType;

    public CInvokeServiceDefinition(String pTitle, int pMethodIndex, EWorkflowTaskType pTaskType, Map<String, IParameter> pInput) {
        this.mTitle = pTitle;
        this.mMethodIndex = pMethodIndex;
        this.mTaskType = pTaskType;
        this.mInput = pInput;
    }

    @Override
    public CInvokeServiceDefinition get() {
        return this;
    }

    @NonNull
    @Override
    public EWorkflowTaskType getWorkflowType() {
        return mTaskType;
    }

    @Override
    public Map<String, IParameter> parameters() {
        return mInput;
    }
}
