package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restflow.core.Network.ERequestSender;
import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSendTask;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Queue;

public class CSend extends IBaseTaskAction {

    private final CSendTask mTask;

    CSend(IWorkflow pWorkflow, CSendTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    /**
     * Sends a collaboration message
     * @param iTaskActions Execution queue
     * @return Boolean value that represents the need to pause execution of this workflow instance until a particular
     * message is received
     */
    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        try {
            ERequestSender.INSTANCE.sendCollaborationJson(mTask.targetSystemUrl(), mTask.createCollaboration(), mWorkflow);
        } catch (JsonProcessingException e) {
            throw new CWorkflowExecutionException(MessageFormat.format(
                    "Cannot parse Json Variable [{0]}]", mTask.sourceVariable().id()));
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
