package com.example.demo;

import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

/**
 * Interface um Workflows zu verwalten
 */
public interface IWorkflowStorage {

    /**
     * Fügt einen Workflow hinzu.
     *
     * @param pWorkflow Workflow-Objekt, dass hinzugefügt werden soll!
     * @return Objektreferenz auf alle Workflows
     */
    @NonNull
    IWorkflowStorage add(@NonNull final IWorkflow pWorkflow);

    /**
     * Entfernt einen Workflow
     *
     * @param pWorkflow Workflow-Objekt, dass entfernt werden soll!
     * @return Objektreferenz auf alle Workflows
     */
    @NonNull
    IWorkflowStorage remove(@NonNull final IWorkflow pWorkflow);
}
