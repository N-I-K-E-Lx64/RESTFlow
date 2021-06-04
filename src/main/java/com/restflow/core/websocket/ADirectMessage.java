package com.restflow.core.websocket;

import java.security.Principal;

public class ADirectMessage extends ASocketMessage {

	/**
	 * Constructor for direct message
	 *
	 * @param data      Array of payloads that
	 * @param endpoint  Websocket endpoint over which this message should be distributed
	 * @param recipient Recipient of the message
	 */
	protected ADirectMessage(Object[] data, String endpoint, Principal recipient) {
		super(data, endpoint);
		// this.recipient = recipient;
	}

	@Override
	public void sendMessage() {

	}
}
