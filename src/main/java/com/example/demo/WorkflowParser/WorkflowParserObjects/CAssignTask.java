package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

public class CAssignTask implements ITask {

    private final EWorkflowTaskType mTaskType;
    private IParameter mSource;
    private IParameter mTarget;

    public CAssignTask(EWorkflowTaskType mTaskType) {
        this.mTaskType = EWorkflowTaskType.ASSIGN;
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

    public IParameter source() {
        return mSource;
    }

    public IParameter target() {
        return mTarget;
    }
}
