package com.restflow.core.WorkflowExecution.Objects;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.lang.NonNull;

public interface IWorkflow extends Consumer<IMessage> {

  @NonNull
  String instance();

  void setInstanceName(@NonNull final String pInstanceName);

  @NonNull
  UUID modelReference();

  @NonNull
  String description();

  @NonNull
  EWorkflowStatus status();

  @NonNull
  ITaskAction currentTask();

  @NonNull
  Map<String, IVariable<?>> variables();

  @NonNull
  List<IParameter<?>> emptyVariables();

  @NonNull
  Queue<ITaskAction> execution();

  @NonNull
  CMonitoringInfo monitoringInfo();

  void setQueue(@NonNull Queue<ITask> pExecution);

  void setVariables(@NonNull Map<String, IVariable<?>> pVariables);

  void setStatus(@NonNull EWorkflowStatus pStatus);

  void setEmptyVariables(@NonNull List<IParameter<?>> pEmptyVariables);

  void generateExecutionOrder(@NonNull Queue<ITask> pTasks);

  Queue<ITask> resetInput(@NonNull Queue<ITask> pTasks);

  Map<String, IVariable<?>> resetVariable(@NonNull Map<String, IVariable<?>> pVariables);

  @NonNull
  IWorkflow start();

  void executeStep();

  void postAction();
}
