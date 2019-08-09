package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CJsonVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CStringVariable;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CInvokeAssign extends IBaseTaskAction {

    private final CInvokeAssignTask mTask;

    CInvokeAssign(IWorkflow pWorkflow, CInvokeAssignTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public void accept(IMessage iMessage) {

    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        if (mTask.target() instanceof CJsonVariable) {
            mTask.target().setValue(mTask.jsonSource());
        } else if (mTask.target() instanceof CStringVariable) {
            mTask.target().setValue(mTask.stringSource());
        }

        return false;
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
