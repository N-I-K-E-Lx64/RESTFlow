package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class CTransferTask extends ATask {

    private final AtomicReference<IVariable<?>> sourceReference = new AtomicReference<>();
    private final AtomicReference<IVariable<?>> targetReference = new AtomicReference<>();

    public CTransferTask(@NonNull final String taskId,
                         @NonNull final String description,
                         @NonNull final IVariable<?> sourceVariable,
                         @NonNull final IVariable<?> targetVariable) {
        super(taskId, description, ETaskType.TRANSFER);

        this.sourceReference.set(sourceVariable);
        this.targetReference.set(targetVariable);
    }

    @Override
    public Object raw() {
        return this;
    }

    public IVariable<?> source() {
        return sourceReference.get();
    }

    public IVariable<?> target() {
        return targetReference.get();
    }
}
