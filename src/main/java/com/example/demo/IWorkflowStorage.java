package com.example.demo;

import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;
import org.springframework.lang.NonNull;

public interface IWorkflowStorage {

    /**
     * Add a new Workflow Object to the Storage
     *
     * @param pWorkflow
     */
    void add(@NonNull final IWorkflow pWorkflow);

    /**
     * Removes a specific Workflow Object
     *
     * @param pWorkflowTitle
     */
    void remove(@NonNull final String pWorkflowTitle);
}
