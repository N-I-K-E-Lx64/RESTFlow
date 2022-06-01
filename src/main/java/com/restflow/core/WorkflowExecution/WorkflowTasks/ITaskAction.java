package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.lang.NonNull;

public interface ITaskAction extends Function<Queue<ITaskAction>, Boolean>, Consumer<IMessage> {

  @NonNull
  String id();
}
