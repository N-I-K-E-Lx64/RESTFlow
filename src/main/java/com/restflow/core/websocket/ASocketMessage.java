package com.restflow.core.websocket;

import com.restflow.core.RESTflowApplication;

/**
 * This base class represents all common functionalities and properties each type of websocket message has to have
 */
public abstract class ASocketMessage {

	/**
	 * The destination endpoint the message will be send to
	 */
	protected String endpoint;

	/**
	 * Payload
	 */
	protected Object[] data;

	protected SimpMessagingManager simpMessagingManager;

	protected ASocketMessage(Object[] data, String endpoint) {
		simpMessagingManager = RESTflowApplication.CGlobal.instance().context().getBean(SimpMessagingManager.class);
		this.data = data;
		this.endpoint = endpoint;
	}

	/**
	 * Send the payload to the specified endpoint
	 */
	public abstract void sendMessage();
}
