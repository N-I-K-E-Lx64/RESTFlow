package com.restflow.core.Controller;

import com.restflow.core.websocket.SimpMessagingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

	@Autowired
	private SimpMessagingManager messagingManager;

	@MessageMapping("/testEndpoint")
	public void sendSpecific(@Payload Message msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
		System.out.println(user.getName());
	}
}
