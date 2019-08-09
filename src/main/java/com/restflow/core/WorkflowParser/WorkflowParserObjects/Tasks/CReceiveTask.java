package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CReceiveTask implements ITask {

    private final String mTitle;
    private final AtomicReference<IVariable> mTargetReference;
    private final Integer mActivityId;

    private final EWorkflowTaskType mTaskType;

    public CReceiveTask(Integer mActivityId, IVariable pTargetVariable) {
        this.mActivityId = mActivityId;
        this.mTargetReference = new AtomicReference<>(pTargetVariable);

        this.mTitle = MessageFormat.format("Stores incoming message in variable [{0}]", pTargetVariable.name());

        this.mTaskType = EWorkflowTaskType.RECEIVE;
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

    public Integer activityId() {
        return mActivityId;
    }

    public IVariable targetVariable() {
        return mTargetReference.get();
    }
}
