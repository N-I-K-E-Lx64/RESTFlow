package com.restflow.core.Network.Objects;

import com.restflow.core.Network.websocket.ABroadcastMessage;
import com.restflow.core.WorkflowExecution.Objects.CMonitoringInfo;

public class CMonitoringMessage extends ABroadcastMessage {

	private static final String MONITORING_ENDPOINT = "/monitoring";

	/**
	 * Create a MonitoringMessage
	 *
	 * @param monitoringInfo Object that contains all relevant parameters that are displayed in the overview
	 */
	public CMonitoringMessage(CMonitoringInfo monitoringInfo) {
		super(new Object[]{monitoringInfo}, MONITORING_ENDPOINT);
	}

	@Override
	public void sendMessage() {
		super.sendMessage();
	}
}
