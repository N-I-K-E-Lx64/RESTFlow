package com.restflow.core;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public interface IWorkflowDefinitions {

    @NonNull
    void add(IWorkflow pWorkflow);

    void remove(String pWorkflow);

    void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow);
}
