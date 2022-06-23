package com.restflow.core.Network.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.restflow.core.Network.IMessage;
import org.springframework.lang.NonNull;

public class CUserParameterMessage implements IMessage {

  private final String instanceId;
  private final String parameterName;
  private final String parameterValue;

  @JsonCreator
  public CUserParameterMessage(@JsonProperty("instanceId") @NonNull final String instanceId,
      @JsonProperty("parameter") @NonNull final String parameter,
      @JsonProperty("value") @NonNull final String value) {
    this.instanceId = instanceId;
    this.parameterName = parameter;
    this.parameterValue = value;
  }

  @JsonGetter("instanceId")
  @Override
  public String getInstanceId() {
    return this.instanceId;
  }

  @JsonGetter("value")
  @Override
  public String get() {
    return this.parameterValue;
  }

  @JsonGetter("parameter")
  public String parameterName() {
    return parameterName;
  }
}
