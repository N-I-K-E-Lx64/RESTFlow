package com.restflow.core.Controller;

import com.restflow.core.Network.Objects.CCollaborationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller class represents the interface through which the system instances can exchange
 * collaboration messages.
 */
@RestController
@RequestMapping("/collaboration")
public class CollaborationController {

  private static final Logger logger = LogManager.getLogger(CollaborationController.class);

  /**
   * Function that assigns incoming collaboration messages to the corresponding workflow instances
   *
   * @param pMessage Collaboration message object
   * @return suitable response
   * @see CCollaborationMessage
   */
  /*@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
  public ResponseEntity<?> retrieveCollaboration(@RequestBody CCollaborationMessage pMessage) {

    EActiveWorkflows.INSTANCE.apply(pMessage.getInstanceId()).accept(pMessage);
    logger.info(pMessage);

    return ResponseEntity.ok().body("Message successfully received!");
  }*/
}
