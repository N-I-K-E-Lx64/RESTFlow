package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

public abstract class IBaseTaskAction implements ITaskAction {

    protected final IWorkflow mWorkflow;

    protected IBaseTaskAction(@NonNull final IWorkflow pWorkflow) {
        this.mWorkflow = pWorkflow;
    }

    @NonNull
    public abstract String id();
}
