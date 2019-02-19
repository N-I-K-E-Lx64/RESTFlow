package com.example.demo.WorkflowParser.WorkflowObjects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Queue;
import java.util.function.Consumer;

public interface IWorkflow extends Consumer<IMessage> {

    @NonNull
    String title();

    Queue<ITaskAction> getQueue();

    void setQueue(@NonNull Queue<ITaskAction> pExecution);

    @NonNull
    IWorkflow start();

    void executeStep();

    void postAction();
}
