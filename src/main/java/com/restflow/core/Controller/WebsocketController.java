package com.restflow.core.Controller;

import com.restflow.core.Network.websocket.ADirectMessage;
import com.restflow.core.Network.websocket.UserIdentifier;
import com.restflow.core.WorkflowExecution.WorkflowService;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

  private static final Logger logger = LogManager.getLogger(ADirectMessage.class);

  private final WorkflowService workflowService;

  @Autowired
  public WebsocketController(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  /**
   * Sends the state of all currently active workflows to the sender of the request only.
   *
   * @param msg       Message to be sent
   * @param sessionId Stomp session
   * @param principal Containing some meta-data from the user. The name (represented by UUID) is
   *                  used here as the identifier
   */
  @MessageMapping("/reqMonitoring")
  public void sendCurrentMonitoringInfo(@Payload Message<?> msg,
      @Header("simpSessionId") String sessionId, Principal principal) {
    logger.info(
        MessageFormat.format("User {0} requested the newest monitoring info", principal.getName()));

    this.workflowService.get()
        .forEach(iWorkflow -> iWorkflow.monitoringInfo()
            .sendDirectMessage(new UserIdentifier(UUID.fromString(principal.getName()))));
  }
}
