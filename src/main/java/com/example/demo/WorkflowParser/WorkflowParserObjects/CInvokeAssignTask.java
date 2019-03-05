package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CInvokeAssignTask implements ITask {

    private final String mTitle;
    private final AtomicReference<IVariable> mTargetReference;
    private JsonNode mJsonSource;

    private final EWorkflowTaskType mTaskType;

    public CInvokeAssignTask(IVariable pTargetReference) {
        mTargetReference = new AtomicReference<>(pTargetReference);

        mTitle = MessageFormat.format("Assign Invoke Result to {0}", mTargetReference.get().name());

        mTaskType = EWorkflowTaskType.INVOKEASSIGN;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public String title() {
        return mTitle;
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
