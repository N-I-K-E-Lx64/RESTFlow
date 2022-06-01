package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import java.util.Queue;
import org.springframework.lang.NonNull;

public class CAssign extends IBaseTaskAction {

  private final CAssignTask mTask;

  CAssign(IWorkflow pWorkflow, CAssignTask pTask) {
    super(pWorkflow);
    mTask = pTask;
  }

  /**
   * Stores a constant parameter in a variable
   *
   * @param iTaskActions Execution queue
   * @return Boolean value that represents the need to pause execution of this workflow instance
   * until a particular message is received (always false)
   */
  @Override
  public Boolean apply(Queue<ITaskAction> iTaskActions) {
    mTask.target().setValue(mTask.source().value().toString());

    // No User Interaction needed
    return false;
  }

  @Override
  public void accept(IMessage iMessage) {

  }

  @NonNull
  @Override
  public String id() {
    return mTask.id();
  }
}
