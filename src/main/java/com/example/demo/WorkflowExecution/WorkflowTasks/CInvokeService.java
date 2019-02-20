package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;

import java.util.Queue;

public class CInvokeService extends IBaseTaskAction {

    private final CInvokeServiceTask mTask;

    public CInvokeService(IWorkflow pWorkflow, CInvokeServiceTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {



        return false;
    }
}
