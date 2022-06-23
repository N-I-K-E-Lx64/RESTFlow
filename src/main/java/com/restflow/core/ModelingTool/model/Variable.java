package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Variable(@JsonGetter("name") String name, @JsonGetter("type") int type) {

  @JsonCreator
  public Variable(@JsonProperty("name") String name, @JsonProperty("type") int type) {
    this.name = name;
    this.type = type;
  }
}
