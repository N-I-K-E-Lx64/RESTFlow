package com.restflow.core.Network.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.RESTflowApplication;

/**
 * This base class represents all common functionalities and properties each type of websocket message must have
 */
public abstract class ASocketMessage {

	protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	protected String endpoint;

	// Message Payload
	protected Object[] data;

	protected MessagingService messagingService;

	/**
	 * @param data     Payload that will be serialized and send
	 * @param endpoint Websocket endpoint over which this message is distributed
	 */
	protected ASocketMessage(Object[] data, String endpoint) {
		messagingService = RESTflowApplication.CGlobal.instance()
				.context()
				.getBean(MessagingService.class);
		this.data = data;
		this.endpoint = endpoint;
	}

	/**
	 * Sends the payload to the specified endpoint
	 */
	public abstract void sendMessage();
}
