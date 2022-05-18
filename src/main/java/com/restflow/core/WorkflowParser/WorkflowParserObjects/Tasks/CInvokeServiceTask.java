package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CInvokeServiceTask extends ATask {

    private final int mResourceIndex;
    private final Map<String, IParameter<?>> mInput;
    private final Api mApi;
    private final AtomicReference<IVariable<?>> mTargetReference = new AtomicReference<>();

    public CInvokeServiceTask(@NonNull final String id,
                              @NonNull final String description,
                              @NonNull final int resourceIndex,
                              @NonNull final Api api,
                              @NonNull final Map<String, IParameter<?>> parameters,
                              @NonNull final IVariable<?> targetVariable) {
        super(id, description, ETaskType.INVOKE);

        this.mResourceIndex = resourceIndex;
        this.mApi = api;
        this.mInput = parameters;
        this.mTargetReference.set(targetVariable);
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    public int resourceIndex() {
        return mResourceIndex;
    }

    @NonNull
    public Api api() {
        return mApi;
    }

    @NonNull
    public Map<String, IParameter<?>> parameters() {
        return mInput;
    }

    @NonNull
    public IVariable<?> target() {
        return mTargetReference.get();
    }

    public void resetInput() {
        System.out.println("Reset");
        // mInput.forEach((key, value) -> value.setValue(null));
    }
}