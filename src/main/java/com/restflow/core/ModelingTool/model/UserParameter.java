package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserParameter(@JsonGetter("userParamId") String id, @JsonGetter("type") int type) {

  @JsonCreator
  public UserParameter(@JsonProperty("userParamId") String id, @JsonProperty("type") int type) {
    this.id = id;
    this.type = type;
  }
}
