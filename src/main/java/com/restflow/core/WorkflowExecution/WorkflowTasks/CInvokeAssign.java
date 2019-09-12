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

    /**
     * Saves the result of a Web service call in a variable
     * @param iTaskActions Execution queue
     * @return Boolean value that represents the need to pause execution of this workflow instance until a particular
     * message is received (always false)
     */
    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        if (mTask.target() instanceof CJsonVariable) {
            mTask.target().setValue(mTask.jsonSource());
        } else if (mTask.target() instanceof CStringVariable) {
            mTask.target().setValue(mTask.stringSource());
        }

        // No User Interaction needed
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
