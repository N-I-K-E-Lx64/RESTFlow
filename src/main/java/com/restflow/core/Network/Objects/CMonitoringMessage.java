package com.restflow.core.Network.Objects;

import com.restflow.core.Network.websocket.ABroadcastMessage;
import com.restflow.core.WorkflowExecution.Objects.CMonitoringInfo;

public class CMonitoringMessage extends ABroadcastMessage {

	private static final String ENDPOINT = "/monitoring";

	/**
	 * Creates a monitoring message that is being broadcasted to all users
	 *
	 * @param update     Payload that will be serialized and send
	 */
	public CMonitoringMessage(CMonitoringInfo update) {
		super(new Object[]{update}, ENDPOINT);
	}

	@Override
	public void sendMessage() {
		super.sendMessage();
	}
}
