package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
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
    public EWorkflowTaskType taskType() {
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
