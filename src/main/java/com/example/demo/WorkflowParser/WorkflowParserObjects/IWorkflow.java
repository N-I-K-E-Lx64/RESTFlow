package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Queue;
import java.util.function.Consumer;

public interface IWorkflow extends Consumer<IMessage> {

    @NonNull
    String name();

    @NonNull
    Queue<ITaskAction> getQueue();

    void setQueue(@NonNull Queue<ITaskAction> pExecution);

    void setWorkflowStatus(@NonNull boolean pIsEverythingOkay);

    void generateExecutionOrder(@NonNull Queue<ITask> pTasks);

    @NonNull
    IWorkflow start();

    void executeStep();

    void postAction();
}
