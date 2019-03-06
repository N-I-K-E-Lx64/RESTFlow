package com.example.demo;

import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import org.springframework.lang.NonNull;

public interface IWorkflowDefinitions {

    @NonNull
    IWorkflowDefinitions add(@NonNull final IWorkflow pWorkflow);

    @NonNull
    IWorkflowDefinitions remove(@NonNull final IWorkflow pWorkflow);
}
