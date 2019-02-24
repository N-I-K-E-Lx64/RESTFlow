package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class CAssignTask implements ITask {

    private final AtomicReference<IParameter> mSourceReference;
    private final AtomicReference<IParameter> mTargetReference;

    private final EWorkflowTaskType mTaskType;
    //TODO : Filter-Möglichkeiten implementieren!

    public CAssignTask(IParameter pVariableReference, IParameter pTargetReference) {
        mSourceReference = new AtomicReference<IParameter>(pVariableReference);
        mTargetReference = new AtomicReference<IParameter>(pVariableReference);

        mTaskType = EWorkflowTaskType.ASSIGN;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public EWorkflowTaskType getWorkflowType() {
        return mTaskType;
    }

    public IParameter source() {
        return mSourceReference.get();
    }

    public IParameter target() {
        return mTargetReference.get();
    }
}
