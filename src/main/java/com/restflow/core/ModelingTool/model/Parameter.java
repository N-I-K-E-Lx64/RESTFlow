package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Parameter(@JsonGetter("value") String value, @JsonGetter("type") int type) {

  public Parameter(@JsonProperty("value") String value, @JsonProperty("type") int type) {
    this.value = value;
    this.type = type;
  }
}
