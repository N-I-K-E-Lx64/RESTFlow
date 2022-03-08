package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CSwitch extends IBaseTaskAction {

    private final CSwitchTask mTask;

    CSwitch(IWorkflow pWorkflow, CSwitchTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    /**
     * Executes the branch condition and changes the execution queue accordingly.
     * @param iTaskActions Execution queue
     * @return Boolean value that represents the need to pause execution of this workflow instance until a particular
     * message is received
     */
    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        // Set Execution to Case
        if (mTask.condition().execute()) {
            mWorkflow.setQueue(mTask.trueFlow());
        } else {
            mWorkflow.setQueue(mTask.falseFlow());
        }

        // No User Interaction needed
        return false;
    }

    @Override
    public void accept(IMessage iMessage) {

    }

    @NonNull
    @Override
    public String id() {
        return mTask.id();
    }
}
