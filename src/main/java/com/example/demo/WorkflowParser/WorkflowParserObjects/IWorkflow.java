package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;

import java.util.Queue;

public interface IWorkflow {

    Queue<ITaskAction> getQueue();

    void setQueue(Queue<ITaskAction> pExecution);
}
