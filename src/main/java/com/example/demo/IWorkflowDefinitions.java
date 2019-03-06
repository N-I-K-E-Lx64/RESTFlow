package com.example.demo;

import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public interface IWorkflowDefinitions {

    @NonNull
    IWorkflowDefinitions add(@NonNull final IWorkflow pWorkflow);

    @NonNull
    IWorkflowDefinitions remove(@NonNull final IWorkflow pWorkflow);

    void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow);
}
