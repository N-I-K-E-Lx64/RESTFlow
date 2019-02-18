package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Queue;

public interface IWorkflow {

    Queue<ITaskAction> getQueue();

    void generateExecutionOrder(@NonNull Queue<ITask> pTasks);
}
