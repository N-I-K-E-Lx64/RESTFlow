package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;

public abstract class IBaseTaskAction implements ITaskAction {

    protected final IWorkflow mWorkflow;

    protected IBaseTaskAction(IWorkflow pWorkflow) {
        this.mWorkflow = pWorkflow;
    }
}
