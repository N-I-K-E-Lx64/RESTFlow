package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.*;
import org.springframework.lang.NonNull;

public enum EWorkflowTaskFactory {

    INSTANCE;

    /**
     * Converts task models into executable task objects
     * @param pWorkflow Corresponding workflow instance
     * @param pTask task model
     * @return Executable task object (ITaskAction)
     * @see ITaskAction
     */
    public ITaskAction factory(@NonNull IWorkflow pWorkflow, @NonNull ITask pTask) {
        switch (pTask.taskType()) {
            case INVOKE:
                return new CInvokeService(pWorkflow, (CInvokeServiceTask) pTask.raw());

            case SWITCH:
                return new CSwitch(pWorkflow, (CSwitchTask) pTask.raw());

            case ASSIGN:
                return new CAssign(pWorkflow, (CAssignTask) pTask.raw());

            case SEND:
                return new CSend(pWorkflow, (CSendTask) pTask.raw());

            case RECEIVE:
                return new CReceive(pWorkflow, (CReceiveTask) pTask.raw());

            default:
                throw new CWorkflowExecutionException("Chosen task is unknown!");
        }
    }
}
