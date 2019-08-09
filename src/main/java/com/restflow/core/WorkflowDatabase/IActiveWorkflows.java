package com.restflow.core.WorkflowDatabase;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

/**
 * Interface um Workflows zu verwalten
 */
public interface IActiveWorkflows {

    @NonNull
    IWorkflow add(@NonNull final String pWorkflowName, @NonNull final IWorkflow pWorkflow);

    @NonNull
    IWorkflow restart(@NonNull final String pWorkflowInstance);

    void remove(@NonNull final String pWorkflow);
}
