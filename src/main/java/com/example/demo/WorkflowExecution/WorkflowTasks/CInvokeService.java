package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceDefinition;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;

import java.util.Queue;

public class CInvokeService extends IBaseTaskAction {

    private final CInvokeServiceDefinition mTask;

    public CInvokeService(IWorkflow pWorkflow, CInvokeServiceDefinition pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        //TODO : Wenn InvokeService Assign Aktion enthält einen neuen AssignTask erstellen.


        return false;
    }
}
