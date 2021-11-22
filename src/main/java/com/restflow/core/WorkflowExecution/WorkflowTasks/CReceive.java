package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CReceiveTask;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Queue;

public class CReceive extends IBaseTaskAction {

    private final CReceiveTask mTask;

    CReceive(IWorkflow pWorkflow, CReceiveTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    /**
     * Checks if the specified variable is empty (or null)
     *
     * @param iTaskActions Execution queue
     * @return Boolean value that represents the need to pause execution of this workflow instance until a particular
     * message is received (if variable is empty = true; else = false)
     */
    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        if (Objects.isNull(mTask.targetVariable().value())) {
            mWorkflow.setStatus(EWorkflowStatus.SUSPENDED);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Processes a collaboration message
     * @param iMessage Collaboration message object
     */
    @Override
    public void accept(IMessage iMessage) {

        CCollaborationMessage lMessage = (CCollaborationMessage) iMessage;

        // Überprüft, ob die ActivityIds gleich sind
        if (lMessage.getActivityId().equals(mTask.activityId())) {
            mTask.targetVariable().setValue(lMessage.get());

            // Ausführung kann fortgesetzt werden
            mWorkflow.setStatus(EWorkflowStatus.ACTIVE);
        }
    }

    @NonNull
    @Override
    public String id() {
        return mTask.id();
    }
}
