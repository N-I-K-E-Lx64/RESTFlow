package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CAssignTask;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CAssign extends IBaseTaskAction {

    private final CAssignTask mTask;

    CAssign(IWorkflow pWorkflow, CAssignTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public void accept(IMessage iMessage) {

    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        mTask.target().setValue(mTask.source().value());

        return false;
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
