package com.restflow.core;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

/**
 * Interface um Workflows zu verwalten
 */
public interface IRunningWorkflows {

    @NonNull
    IWorkflow add(@NonNull final String pWorkflowName, @NonNull final IWorkflow pWorkflow);

    @NonNull
    IRunningWorkflows remove(@NonNull final String pWorkflow);
}
