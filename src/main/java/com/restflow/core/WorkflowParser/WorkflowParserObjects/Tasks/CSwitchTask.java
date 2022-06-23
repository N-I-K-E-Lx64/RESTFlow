package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ICondition;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import java.util.Queue;
import java.util.UUID;
import org.springframework.lang.NonNull;

public class CSwitchTask extends ATask {

  private final ICondition mCondition;
  private final Queue<ITask> mTrueFlow;
  private final Queue<ITask> mFalseFlow;

  public CSwitchTask(@NonNull final UUID taskId,
      @NonNull final String title,
      @NonNull final String description,
      @NonNull final ICondition condition,
      @NonNull final Queue<ITask> trueFlow,
      @NonNull final Queue<ITask> falseFlow) {
    super(taskId, title, description, ETaskType.SWITCH);

    this.mCondition = condition;
    this.mTrueFlow = trueFlow;
    this.mFalseFlow = falseFlow;
  }

  @NonNull
  @Override
  public Object raw() {
    return this;
  }

  @NonNull
  public ICondition condition() {
    return mCondition;
  }

  @NonNull
  public Queue<ITask> trueFlow() {
    return mTrueFlow;
  }

  @NonNull
  public Queue<ITask> falseFlow() {
    return mFalseFlow;
  }
}
