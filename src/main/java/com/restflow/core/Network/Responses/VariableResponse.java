package com.restflow.core.Network.Responses;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

public class VariableResponse {

  private final String name;
  private final String value;
  @JsonProperty("type")
  private final String variableType;

  public VariableResponse(@NonNull final String pName,
      @NonNull final String pVariableType,
      @NonNull final String pValue) {
    this.name = pName;
    this.variableType = pVariableType;
    this.value = pValue;
  }

  public String getName() {
    return name;
  }

  @JsonGetter("type")
  public String getVariableType() {
    return variableType;
  }

  public String getValue() {
    return value;
  }
}
