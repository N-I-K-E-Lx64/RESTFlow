package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CAssignTask implements ITask {

    private IParameter mSourceParameter;
    private AtomicReference<IVariable> mTargetReference;

    private final EWorkflowTaskType mTaskType;

    public CAssignTask() {
        mTaskType = EWorkflowTaskType.ASSIGN;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public String title() {
        return MessageFormat.format("Assign {0} to {1}", mSourceParameter.id(), mTargetReference.get().name());
    }

    @NonNull
    @Override
    public EWorkflowTaskType taskType() {
        return mTaskType;
    }

    public IParameter source() {
        return mSourceParameter;
    }

    public void setSource(IParameter pSourceParameter) {
        this.mSourceParameter = pSourceParameter;
    }

    public void setTarget(IVariable pTargetReference) {
        this.mTargetReference = new AtomicReference<>(pTargetReference);
    }

    public IVariable target() {
        return mTargetReference.get();
    }
}
