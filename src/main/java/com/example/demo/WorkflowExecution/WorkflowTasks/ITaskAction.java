package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import org.springframework.lang.NonNull;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO : Delete the Function and create a method which returns a boolean
public interface ITaskAction extends Function<Queue<ITaskAction>, Boolean>, Consumer<IMessage> {

    @NonNull
    String title();
}
