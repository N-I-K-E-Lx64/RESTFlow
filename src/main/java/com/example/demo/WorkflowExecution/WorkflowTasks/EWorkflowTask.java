package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowObjects.CInvokeServiceDefinition;
import com.example.demo.WorkflowParser.WorkflowObjects.CWorkflow;
import com.example.demo.WorkflowParser.WorkflowObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.stream.Stream;

public enum EWorkflowTask {

    INSTANCE;

    public Stream<ITaskAction> factory(@NonNull final EWorkflowTaskDefinition pDecision, @NonNull CWorkflow pWorkflow, @NonNull ITask pTask) {
        switch (pDecision) {
            case INVOKESERVICE:
                return Stream.of(new CInvokeService(pWorkflow, (CInvokeServiceDefinition) pTask.get()));

            default:
                throw new RuntimeException("Chosen task is unknown!");
        }
    }
}
