package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CAssignTask implements ITask {

    private final String mTitle;
    private final IParameter mSourceParameter;
    private final AtomicReference<IVariable> mTargetReference;

    private final EWorkflowTaskType mTaskType;
    //TODO : Filter-Möglichkeiten implementieren!

    public CAssignTask(IParameter pSourceParameter, IVariable pTargetReference) {
        mSourceParameter = pSourceParameter;
        mTargetReference = new AtomicReference<>(pTargetReference);

        mTitle = MessageFormat.format("Assign {0} to {1}", mSourceParameter.name(), mTargetReference.get().name());

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

    public IParameter source() {
        return mSourceParameter;
    }

    public IVariable target() {
        return mTargetReference.get();
    }
}
