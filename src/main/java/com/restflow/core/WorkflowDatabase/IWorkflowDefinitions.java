package com.restflow.core.WorkflowDatabase;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import java.util.Queue;
import org.springframework.lang.NonNull;

public interface IWorkflowDefinitions {

  @NonNull
  void add(IWorkflow pWorkflow);

  void remove(String pWorkflow);

  void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow);
}
