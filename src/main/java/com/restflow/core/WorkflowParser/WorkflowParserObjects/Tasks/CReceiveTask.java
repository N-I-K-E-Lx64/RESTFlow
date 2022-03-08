package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

// Apply the new Model structure here!
public class CReceiveTask extends ATask {

    private final AtomicReference<IVariable<?>> mTargetReference = new AtomicReference<>();
    private final Integer mActivityId;

    public CReceiveTask(@NonNull final String taskId,
                        @NonNull final String description,
                        @NonNull final Integer activityId,
                        @NonNull final IVariable<?> targetVariable) {
        super(taskId, description, ETaskType.RECEIVE);

        this.mActivityId = activityId;
        this.mTargetReference.set(targetVariable);
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    public Integer activityId() {
        return mActivityId;
    }

    public IVariable<?> targetVariable() {
        return mTargetReference.get();
    }
}
