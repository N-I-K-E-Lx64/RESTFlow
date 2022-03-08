package com.restflow.core.Network.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restflow.core.Network.IMessage;

public class CCollaborationMessage implements IMessage {

    @JsonProperty("workflow")
    private String workflowInstance;
    @JsonProperty("payload")
    private String payload;
    @JsonProperty("activity")
    private Integer activityId;

    public CCollaborationMessage(String pWorkflowInstance, String pPayload, Integer pActivityId) {
        this.workflowInstance = pWorkflowInstance;
        this.payload = pPayload;
        this.activityId = pActivityId;
    }

    @Override
    public String getInstanceId() {
        return workflowInstance;
    }

    @Override
    public String get() {
        return payload;
    }

    public Integer getActivityId() {
        return activityId;
    }


}
