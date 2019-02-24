package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CAssignTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;

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
}
