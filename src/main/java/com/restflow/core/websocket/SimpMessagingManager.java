package com.restflow.core.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SimpMessagingManager {

	// Socket
	private final SimpMessagingTemplate websocket;

	@Autowired
	public SimpMessagingManager(SimpMessagingTemplate websocket) {
		this.websocket = websocket;
	}

	/**
	 * Send a message to a specific user via a specific endpoint
	 *
	 * @param user     UserId of recipient
	 * @param endpoint Endpoint
	 * @param data     Message to be sent
	 */
	public final void sendTo(@NonNull final String user, @NonNull final String endpoint, @NonNull final Object data) {
		System.out.println(user);
		websocket.convertAndSendToUser(user, endpoint, data);
	}

	/**
	 * Broadcast a message to all users listening on a specific endpoint
	 *
	 * @param endpoint Endpoint
	 * @param data     Message to be sent
	 */
	public final void sendTo(@NonNull final String endpoint, @NonNull final Object data) {
		this.websocket.convertAndSend(endpoint, data);
	}
}
