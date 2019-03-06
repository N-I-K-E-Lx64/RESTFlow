package com.restflow.core;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

/**
 * Interface um Workflows zu verwalten
 */
public interface IRunningWorkflows {

    /**
     * Fügt einen Workflow hinzu.
     *
     * @param pWorkflow Workflow-Objekt, dass hinzugefügt werden soll!
     * @return Objektreferenz auf alle Workflows
     */
    @NonNull
    IWorkflow add(@NonNull final IWorkflow pWorkflow);

    /**
     * Entfernt einen Workflow
     *
     * @param pWorkflow Workflow-Objekt, dass entfernt werden soll!
     * @return Objektreferenz auf alle Workflows
     */
    @NonNull
    IRunningWorkflows remove(@NonNull final String pWorkflow);
}
