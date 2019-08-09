package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.IMessage;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CReceiveTask;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Queue;

public class CReceive extends IBaseTaskAction {

    private final CReceiveTask mTask;

    CReceive(IWorkflow pWorkflow, CReceiveTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        if (Objects.isNull(mTask.targetVariable().value())) {
            mWorkflow.setStatus(EWorkflowStatus.SUSPENDED);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void accept(IMessage iMessage) {

        CCollaborationMessage lMessage = (CCollaborationMessage) iMessage;

        if (lMessage.getActivityId().equals(mTask.activityId())) {

            switch (mTask.targetVariable().variableType()) {
                case JSON:
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        mTask.targetVariable().setValue(mapper.readTree(lMessage.get()));
                    } catch (IOException e) {
                        throw new CWorkflowExecutionException("Conversion of the payload JSON String into a JSON Node failed!");
                    }
                    break;

                case STRING:
                    mTask.targetVariable().setValue(lMessage.get());
                    break;

                case INTEGER:
                    mTask.targetVariable().setValue(Integer.parseInt(lMessage.get()));
                    break;
            }

            mWorkflow.setStatus(EWorkflowStatus.ACTIVE);
        }
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
