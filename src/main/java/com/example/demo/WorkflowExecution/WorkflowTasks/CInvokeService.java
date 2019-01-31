package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowObjects.CInvokeServiceDefinition;

import java.util.Queue;

public class CInvokeService implements ITaskAction {

    private final CInvokeServiceDefinition mTask;

    public CInvokeService(CInvokeServiceDefinition pTask) {
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        //TODO : Wenn InvokeService Assign Aktion enthält einen neuen AssignTask erstellen.


        return false;
    }
}
