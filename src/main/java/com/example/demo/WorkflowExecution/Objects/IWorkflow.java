package com.example.demo.WorkflowExecution.Objects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public interface IWorkflow extends Consumer<IMessage> {

    @NonNull
    String name();

    @NonNull
    EWorkflowStatus status();

    @NonNull
    ITaskAction currentTask();

    @NonNull
    Map<String, IVariable> variables();

    @NonNull
    List<String> emptyVariables();

    @NonNull
    Queue<ITaskAction> getQueue();

    void setQueue(@NonNull Queue<ITaskAction> pExecution);

    void setStatus(@NonNull EWorkflowStatus pStatus);

    void setEmptyVariables(@NonNull List<String> pEmptyVariables);

    void generateExecutionOrder(@NonNull Queue<ITask> pTasks);

    @NonNull
    IWorkflow start();

    void executeStep();

    void postAction();
}
