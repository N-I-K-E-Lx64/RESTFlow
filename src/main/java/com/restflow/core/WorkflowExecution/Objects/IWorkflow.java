package com.restflow.core.WorkflowExecution.Objects;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public interface IWorkflow extends Consumer<IMessage> {

    @NonNull
    String title();

    @NonNull
    String description();

    @NonNull
    EWorkflowStatus status();

    @NonNull
    ITaskAction currentTask();

    @NonNull
    Map<String, IVariable> variables();

    @NonNull
    List<String> emptyVariables();

    @NonNull
    Queue<ITaskAction> execution();

    void setQueue(@NonNull Queue<ITask> pExecution);

    void setStatus(@NonNull EWorkflowStatus pStatus);

    void setEmptyVariables(@NonNull List<String> pEmptyVariables);

    void generateExecutionOrder(@NonNull Queue<ITask> pTasks);

    Queue<ITask> resetInput(@NonNull Queue<ITask> pTasks);

    Map<String, IVariable> resetVariable(@NonNull Map<String, IVariable> pVariables);

    @NonNull
    IWorkflow start();

    void executeStep();

    void postAction();
}
