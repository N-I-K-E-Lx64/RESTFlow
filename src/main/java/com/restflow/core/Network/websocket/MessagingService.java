package com.restflow.core.Network.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class MessagingService {

	private static final Logger logger = LogManager.getLogger(MessagingService.class);

	private final SimpMessagingTemplate websocket;



	@Autowired
	public MessagingService(SimpMessagingTemplate websocket) {
		this.websocket = websocket;
	}

	/**
	 * Send a message to a specific user on a specified endpoint
	 * @param user UserID of recipient
	 * @param endpoint Endpoint
	 * @param data Message payload to be sent
	 */
	public final void sendTo(@NonNull final String user, @NonNull final String endpoint, @NonNull final Object data) {
		logger.info(MessageFormat.format("Sending direct message to user {0} over {1}", user, endpoint));
		websocket.convertAndSendToUser(user, endpoint, data);
	}

	/**
	 * Broadcast a message to all clients listening on a specified endpoint
	 *
	 * @param endpoint Endpoint
	 * @param data     Message payload to be sent
	 */
	public final void sendTo(@NonNull final String endpoint, @NonNull final Object data) {
		logger.info("Sending broadcast message to " + endpoint);
		websocket.convertAndSend(endpoint, data);
	}
}
