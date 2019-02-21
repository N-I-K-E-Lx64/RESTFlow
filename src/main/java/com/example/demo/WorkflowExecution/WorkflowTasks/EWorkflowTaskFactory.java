package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

public enum EWorkflowTaskFactory {

    INSTANCE;

    public ITaskAction factory(@NonNull CWorkflow pWorkflow, @NonNull ITask pTask) {
        switch (pTask.getWorkflowType()) {
            case INVOKESERVICE:
                return new CInvokeService(pWorkflow, (CInvokeServiceTask) pTask.get());

            case SWITCH:

            default:
                throw new RuntimeException("Chosen task is unknown!");
        }
    }
}
