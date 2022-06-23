package com.restflow.core.Network.Objects;

import com.restflow.core.Network.websocket.ADirectMessage;
import com.restflow.core.Network.websocket.UserIdentifier;
import com.restflow.core.WorkflowExecution.Objects.CMonitoringInfo;

public class CDirectMonitoringMessage extends ADirectMessage {

  private static final String ENDPOINT = "/monitoring";

  /**
   * Create a MonitoringMessage that is being sent directly to the specified recipient
   *
   * @param update    Object that contains all relevant parameters that are displayed in the
   *                  overview
   * @param recipient Recipient of this message
   */
  public CDirectMonitoringMessage(CMonitoringInfo update, UserIdentifier recipient) {
    super(new Object[]{update}, ENDPOINT, recipient);
  }

  @Override
  public void sendMessage() {
    super.sendMessage();
  }
}
