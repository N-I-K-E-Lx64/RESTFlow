package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import org.springframework.lang.NonNull;

public interface ITask {

    Object raw();

    @NonNull
    String id();

    @NonNull
    String description();

    @NonNull
    ETaskType taskType();

}
