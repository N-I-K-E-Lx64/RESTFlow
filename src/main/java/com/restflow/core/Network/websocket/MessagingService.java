package com.restflow.core.Network.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	private final SimpMessagingTemplate websocket;

	@Autowired
	public MessagingService(SimpMessagingTemplate websocket) {
		this.websocket = websocket;
	}

	/**
	 * Broadcast a message to all clients listening on a specified endpoint
	 *
	 * @param endpoint Endpoint
	 * @param data     Message payload to be sent
	 */
	public final void sendTo(@NonNull final String endpoint, @NonNull final Object data) {
		websocket.convertAndSend(endpoint, data);
	}
}
