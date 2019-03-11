package com.restflow.core.WorkflowExecution.WorkflowTasks;


import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import org.springframework.lang.NonNull;

public enum EWorkflowTaskFactory {

    INSTANCE;

    public ITaskAction factory(@NonNull IWorkflow pWorkflow, @NonNull ITask pTask) {
        switch (pTask.taskType()) {
            case INVOKESERVICE:
                return new CInvokeService(pWorkflow, (CInvokeServiceTask) pTask.raw());

            case SWITCH:
                return new CSwitch(pWorkflow, (CSwitchTask) pTask.raw());

            case ASSIGN:
                return new CAssign(pWorkflow, (CAssignTask) pTask.raw());

            case INVOKEASSIGN:
                return new CInvokeAssign(pWorkflow, (CInvokeAssignTask) pTask.raw());

            default:
                throw new RuntimeException("Chosen task is unknown!");
        }
    }
}
