package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ITaskAction extends Function<Queue<ITaskAction>, Boolean>, Consumer<IMessage> {
}
