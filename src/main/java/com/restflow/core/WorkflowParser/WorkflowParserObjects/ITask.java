package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

public interface ITask<T> {

    @NonNull
    T raw();

    @NonNull
    String title();

    @NonNull
    EWorkflowTaskType taskType();

}
