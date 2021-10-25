package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

public interface ITask {

    @NonNull
    Object raw();

    @NonNull
    String title();

    @NonNull
    EWorkflowTaskType taskType();

}
