package com.example.demo.WorkflowExecution.WorkflowTasks;

import java.util.Queue;
import java.util.function.Function;

public interface ITaskAction extends Function<Queue<ITaskAction>, Boolean> {
}
