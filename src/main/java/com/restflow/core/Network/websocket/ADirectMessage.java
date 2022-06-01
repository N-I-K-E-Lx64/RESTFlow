package com.restflow.core.Network.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ADirectMessage extends ASocketMessage {

  private static final Logger logger = LogManager.getLogger(ADirectMessage.class);
  private static final String DIRECT_MESSAGE_PREFIX = "/queue";

  protected UserIdentifier recipient;

  /**
   * CTR of a direct message
   *
   * @param data      Payload that will be serialized and send
   * @param endpoint  Websocket endpoint over which this message is distributed
   * @param recipient Recipient of this message
   */
  protected ADirectMessage(Object[] data, String endpoint, UserIdentifier recipient) {
    super(data, DIRECT_MESSAGE_PREFIX + endpoint);
    this.recipient = recipient;
  }

  /**
   * Sends the message
   */
  @Override
  public void sendMessage() {
    Arrays.stream(data).forEach(this::sendSingleMessage);
  }

  /**
   * Sends a single element of the payload array
   *
   * @param i Object to be sent
   */
  private void sendSingleMessage(Object i) {
    try {
      logger.info(
          "Sending message: " + JSON_MAPPER.writeValueAsString(i) + " to Endpoint: " + endpoint);
      messagingService.sendTo(recipient.identifier(), endpoint, JSON_MAPPER.writeValueAsString(i));
    } catch (JsonProcessingException e) {
      logger.error(e);
    }
  }
}
