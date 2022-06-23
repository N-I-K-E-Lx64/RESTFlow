package com.restflow.core.WorkflowExecution.Objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.restflow.core.Network.Objects.CDirectMonitoringMessage;
import com.restflow.core.Network.Objects.CMonitoringMessage;
import com.restflow.core.Network.websocket.UserIdentifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.lang.NonNull;

@JsonPropertyOrder({"wfName", "wfStatus", "currentActivity", "startTime"})
public class CMonitoringInfo implements IMonitoringInfo {

  private final String workflowName;
  private final String startTime;
  private String currentActivity;
  private EWorkflowStatus workflowStatus;

  public CMonitoringInfo(@NonNull final String workflowName,
      @NonNull final LocalDateTime unformattedStartTime) {
    this.workflowName = workflowName;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    this.startTime = unformattedStartTime.format(dateTimeFormatter);

    // Set the initial status to ACTIVE
    this.workflowStatus = EWorkflowStatus.ACTIVE;
  }

  public IMonitoringInfo setCurrentActivity(@NonNull String currentActivity) {
    this.currentActivity = currentActivity;
    return this;
  }

  public IMonitoringInfo setWorkflowStatus(@NonNull EWorkflowStatus workflowStatus) {
    this.workflowStatus = workflowStatus;
    return this;
  }

  @JsonGetter("wfName")
  public String getWorkflowName() {
    return workflowName;
  }

  @JsonGetter("currentActivity")
  public String getCurrentActivity() {
    return currentActivity;
  }

  @JsonGetter("wfStatus")
  public EWorkflowStatus getWorkflowStatus() {
    return workflowStatus;
  }

  @JsonGetter("startTime")
  public String getStartTime() {
    return startTime;
  }

  /**
   * Creates a broadcast message containing the workflow state
   *
   * @see CMonitoringMessage
   */
  @Override
  public void sendMessage() {
    CMonitoringMessage monitoringMessage = new CMonitoringMessage(this);
    monitoringMessage.sendMessage();
  }

  /**
   * Creates a direct message containing the workflow state and sends it to a specific user
   *
   * @see CDirectMonitoringMessage
   */
  public void sendDirectMessage(UserIdentifier recipient) {
    CDirectMonitoringMessage monitoringMessage = new CDirectMonitoringMessage(this, recipient);
    monitoringMessage.sendMessage();
  }
}
