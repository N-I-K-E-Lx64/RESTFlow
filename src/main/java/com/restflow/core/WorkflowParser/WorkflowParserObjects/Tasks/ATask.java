package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

public abstract class ATask implements ITask {

    protected String mId;
    protected String mDescription;
    protected ETaskType mType;

    protected ATask(@NonNull final String taskId,
                    @NonNull final String description,
                    @NonNull final ETaskType type) {
        this.mId = taskId;
        this.mDescription = description;
        this.mType = type;
    }

    /**
     * Returns itself!
     *
     * @return The class itself!
     */
    public abstract Object raw();

    @NonNull
    @Override
    public String id() {
        return this.mId;
    }

    @NonNull
    @Override
    public String description() {
        return this.mDescription;
    }

    @NonNull
    @Override
    public ETaskType taskType() {
        return this.mType;
    }
}
