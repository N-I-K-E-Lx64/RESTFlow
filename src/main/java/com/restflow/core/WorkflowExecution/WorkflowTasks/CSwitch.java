package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CSwitch extends IBaseTaskAction {

    private final CSwitchTask mTask;

    protected CSwitch(IWorkflow pWorkflow, CSwitchTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        // Set Execution to Case
        if (mTask.condition().execute()) {
            mWorkflow.setQueue(mTask.caseExecution());
        } else {
            mWorkflow.setQueue(mTask.elseExecution());
        }

        return false;
    }

    @Override
    public void accept(IMessage iMessage) {

    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
