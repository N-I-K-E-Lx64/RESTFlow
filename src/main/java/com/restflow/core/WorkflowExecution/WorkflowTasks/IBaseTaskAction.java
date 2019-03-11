package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;

public abstract class IBaseTaskAction implements ITaskAction {

    protected final IWorkflow mWorkflow;

    protected IBaseTaskAction(IWorkflow pWorkflow) {
        this.mWorkflow = pWorkflow;
    }
}
