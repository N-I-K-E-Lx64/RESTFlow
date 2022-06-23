package com.restflow.core.Network.Responses;

public class WorkflowListResponse {

  private final String workflowName;
  private final String modelName;
  private final String currentStatus;
  private final String currentTask;


  public WorkflowListResponse(String workflowName, String modelName, String currentStatus,
      String currentTask) {
    this.workflowName = workflowName;
    this.modelName = modelName;
    this.currentStatus = currentStatus;
    this.currentTask = currentTask;
  }

  public String getWorkflowName() {
    return workflowName;
  }

  public String getModelName() {
    return modelName;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public String getCurrentTask() {
    return currentTask;
  }
}
