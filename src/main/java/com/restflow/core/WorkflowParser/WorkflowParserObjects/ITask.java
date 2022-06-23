package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import java.util.UUID;
import org.springframework.lang.NonNull;

public interface ITask {

  Object raw();

  @NonNull
  UUID id();

  @NonNull
  String title();

  @NonNull
  String description();

  @NonNull
  ETaskType taskType();

}
