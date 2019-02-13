package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;

import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ITaskAction extends Function<Queue<ITaskAction>, Boolean>, BiConsumer<Queue<ITaskAction>, IMessage> {
}
