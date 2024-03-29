package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

public class CInvokeServiceTask extends ATask {

  private final int mResourceIndex;
  private final Map<String, IParameter<?>> mInput;
  private final Api mApi;
  private final AtomicReference<IVariable<?>> mTargetReference = new AtomicReference<>();

  public CInvokeServiceTask(@NonNull final UUID id,
      @NonNull final String name,
      @NonNull final String description,
      @NonNull final int resourceIndex,
      @NonNull final Api api,
      @NonNull final Map<String, IParameter<?>> parameters,
      @NonNull final IVariable<?> targetVariable) {
    super(id, name, description, ETaskType.INVOKE);

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
    mInput.forEach((key, value) -> value.setValue(null));
  }
}