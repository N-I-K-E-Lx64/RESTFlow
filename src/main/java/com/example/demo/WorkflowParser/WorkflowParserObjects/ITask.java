package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

import java.util.Map;

public interface ITask<T> {

    T get();

    @NonNull
    EWorkflowTaskType getWorkflowType();

    @NonNull
    Map<String, IParameter> parameters();

}
