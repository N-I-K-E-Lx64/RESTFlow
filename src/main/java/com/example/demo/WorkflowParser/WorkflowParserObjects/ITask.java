package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

public interface ITask<T> {

    @NonNull
    T get();

    @NonNull
    EWorkflowTaskType getWorkflowType();

}
