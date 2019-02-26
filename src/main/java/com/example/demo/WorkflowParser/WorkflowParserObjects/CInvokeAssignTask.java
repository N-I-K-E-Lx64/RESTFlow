package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class CInvokeAssignTask implements ITask {

    private final AtomicReference<IVariable> mTargetReference;
    private final EWorkflowTaskType mTaskType;
    private JsonNode mJsonSource;

    public CInvokeAssignTask(IVariable pTargetReference) {
        this.mTargetReference = new AtomicReference<>(pTargetReference);

        mTaskType = EWorkflowTaskType.INVOKEASSIGN;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public EWorkflowTaskType getWorkflowType() {
        return mTaskType;
    }

    public void setJsonSource(JsonNode pJsonSource) {
        mJsonSource = pJsonSource;
    }

    @NonNull
    public IVariable target() {
        return mTargetReference.get();
    }

    @NonNull
    public JsonNode jsonSource() {
        return mJsonSource;
    }
}
