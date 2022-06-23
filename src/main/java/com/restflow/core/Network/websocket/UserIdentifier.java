package com.restflow.core.Network.websocket;

import java.util.UUID;

public class UserIdentifier {

  private final UUID uuid;

  public UserIdentifier(UUID uuid) {
    this.uuid = uuid;
  }

  public String identifier() {
    return this.uuid.toString();
  }
}
