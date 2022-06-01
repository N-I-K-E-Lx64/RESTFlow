package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.NonNull;

public class CSendTask extends ATask {

  private static final ObjectMapper mapper = new ObjectMapper();

  private final String mTargetSystemUrl;
  private final String mTargetWorkflow;
  private final AtomicReference<IVariable<?>> mSourceReference = new AtomicReference<>();
  private final Integer mActivityId;

  public CSendTask(@NonNull final String taskId,
      @NonNull final String description,
      @NonNull final String targetSystemUrl,
      @NonNull final String targetWorkflowInstance,
      @NonNull final IVariable<?> sourceVariable,
      @NonNull final Integer activityId) {
    super(taskId, description, ETaskType.SEND);

    this.mTargetSystemUrl = targetSystemUrl;
    this.mTargetWorkflow = targetWorkflowInstance;
    this.mSourceReference.set(sourceVariable);
    this.mActivityId = activityId;
  }

  @NonNull
  @Override
  public Object raw() {
    return this;
  }

  @NonNull
  public String targetSystemUrl() {
    return mTargetSystemUrl;
  }

  @NonNull
  public IVariable<?> sourceVariable() {
    return mSourceReference.get();
  }

  @NonNull
  public CCollaborationMessage createCollaboration() throws JsonProcessingException {
    String lVariableValue = mapper.writeValueAsString(mSourceReference.get().value());

    return new CCollaborationMessage(mTargetWorkflow, lVariableValue, mActivityId);
  }
}
