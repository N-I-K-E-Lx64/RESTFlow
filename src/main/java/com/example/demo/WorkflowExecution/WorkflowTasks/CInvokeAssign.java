package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeAssignTask;
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
        mTask.target().setValue(mTask.jsonSource());

        return false;
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
