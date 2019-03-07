package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CCondition;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CSwitchTask implements ITask {

    private final String mTitle;
    private final EWorkflowTaskType mTaskType;

    private CCondition mCondition;
    private Queue<ITask> mCaseExecution;
    private Queue<ITask> mElseExecution;

    public CSwitchTask(String pTitle) {
        this.mTitle = pTitle;

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

    public void setCondition(@NonNull final CCondition pCondition) {
        this.mCondition = pCondition;
    }

    @NonNull
    public CCondition condition() {
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
