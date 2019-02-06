package com.example.demo.WorkflowParser.WorkflowObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Queue;

public interface IWorkflow {

    @NonNull
    String title();

    Queue<ITaskAction> getQueue();

    void setQueue(Queue<ITaskAction> pExecution);
}
