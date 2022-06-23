package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.NonNull;

public class CAssignTask extends ATask {

  private final IParameter<?> mSourceParameter;
  private final AtomicReference<IVariable<?>> mTargetReference = new AtomicReference<>();

  public CAssignTask(@NonNull final UUID pId,
      @NonNull final String pName,
      @NonNull final String pDescription,
      @NonNull final IParameter<?> pSourceParameter,
      @NonNull final IVariable<?> pTargetVariable) {
    super(pId, pName, pDescription, ETaskType.ASSIGN);

    this.mSourceParameter = pSourceParameter;
    this.mTargetReference.set(pTargetVariable);
  }

  @NonNull
  @Override
  public Object raw() {
    return this;
  }

  public IParameter<?> source() {
    return mSourceParameter;
  }

  public IVariable<?> target() {
    return mTargetReference.get();
  }
}
