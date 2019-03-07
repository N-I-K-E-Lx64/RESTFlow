package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CAssignTask implements ITask {

    private final String mTitle;
    private final AtomicReference<IVariable> mSourceReference;
    private final AtomicReference<IVariable> mTargetReference;

    private final EWorkflowTaskType mTaskType;
    //TODO : Filter-Möglichkeiten implementieren!

    public CAssignTask(IVariable pVariableReference, IVariable pTargetReference) {
        mSourceReference = new AtomicReference<>(pVariableReference);
        mTargetReference = new AtomicReference<>(pVariableReference);

        mTitle = MessageFormat.format("Assign {0} to {1}", mSourceReference.get().name(), mTargetReference.get().name());

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
        return mTitle;
    }

    @NonNull
    @Override
    public EWorkflowTaskType taskType() {
        return mTaskType;
    }

    public IVariable source() {
        return mSourceReference.get();
    }

    public IVariable target() {
        return mTargetReference.get();
    }
}
