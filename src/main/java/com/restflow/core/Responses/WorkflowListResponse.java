package com.restflow.core.Responses;

public class WorkflowListResponse {

    private final String workflowName;
    private final String modelName;
    private final String currentTask;


    public WorkflowListResponse(String workflowName, String modelName, String currentTask) {
        this.workflowName = workflowName;
        this.modelName = modelName;
        this.currentTask = currentTask;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCurrentTask() {
        return currentTask;
    }
}
