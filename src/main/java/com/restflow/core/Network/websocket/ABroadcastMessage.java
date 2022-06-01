package com.restflow.core.Network.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABroadcastMessage extends ASocketMessage {

  private static final Logger logger = LoggerFactory.getLogger(ABroadcastMessage.class);

  private static final String BROADCAST_MESSAGE_ENDPOINT = "/topic";

  /**
   * Constructor of a broadcast message
   *
   * @param data     Payload that will be serialized and send
   * @param endpoint Websocket endpoint over which this message is distributed
   */
  protected ABroadcastMessage(Object[] data, String endpoint) {
    super(data, BROADCAST_MESSAGE_ENDPOINT + endpoint);
  }

  /**
   * Send this message
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
      messagingService.sendTo(endpoint, JSON_MAPPER.writeValueAsString(i));
    } catch (final JsonProcessingException e) {
      logger.error(e.getMessage());
    }
  }
}
