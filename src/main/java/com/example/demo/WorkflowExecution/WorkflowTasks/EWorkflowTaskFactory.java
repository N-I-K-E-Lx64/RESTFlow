package com.example.demo.WorkflowExecution.WorkflowTasks;


import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CAssignTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeAssignTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

public enum EWorkflowTaskFactory {

    INSTANCE;

    public ITaskAction factory(@NonNull IWorkflow pWorkflow, @NonNull ITask pTask) {
        switch (pTask.getWorkflowType()) {
            case INVOKESERVICE:
                return new CInvokeService(pWorkflow, (CInvokeServiceTask) pTask.raw());

            case SWITCH:

            case ASSIGN:
                return new CAssign(pWorkflow, (CAssignTask) pTask.raw());

            case INVOKEASSIGN:
                return new CInvokeAssign(pWorkflow, (CInvokeAssignTask) pTask.raw());

            default:
                throw new RuntimeException("Chosen task is unknown!");
        }
    }
}
