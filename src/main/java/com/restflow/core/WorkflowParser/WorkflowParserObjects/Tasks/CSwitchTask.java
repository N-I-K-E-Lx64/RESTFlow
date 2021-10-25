package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ICondition;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CSwitchTask implements ITask {

    private final String mTitle;

    private ICondition mCondition;
    private final Queue<ITask> mCaseExecution;
    private final Queue<ITask> mElseExecution;

    private final EWorkflowTaskType mTaskType;

    //TODO : Title erstellen
    public CSwitchTask(@NonNull final ICondition pCondition, @NonNull final Queue<ITask> pCase, @NonNull final Queue<ITask> pElse) {
        this.mTitle = "Switch Task";
        this.mCondition = pCondition;
        this.mCaseExecution = pCase;
        this.mElseExecution = pElse;

        this.mTaskType = EWorkflowTaskType.SWITCH;
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

    public void setCondition(@NonNull final ICondition pCondition) {
        this.mCondition = pCondition;
    }

    @NonNull
    public ICondition condition() {
        return mCondition;
    }

    @NonNull
    public Queue<ITask> caseExecution() {
        return mCaseExecution;
    }

    @NonNull
    public Queue<ITask> elseExecution() {
        return mElseExecution;
    }
}
