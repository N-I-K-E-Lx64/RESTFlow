package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceDefinition;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;

import java.util.stream.Stream;

public enum EWorkflowTaskFactory {

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
