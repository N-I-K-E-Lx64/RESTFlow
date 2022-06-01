package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Variable {

  private final String name;
  private final int type;

  @JsonCreator
  public Variable(@JsonProperty("name") String name, @JsonProperty("type") int type) {
    this.name = name;
    this.type = type;
  }

  @JsonGetter("name")
  public String name() {
    return name;
  }

  @JsonGetter("type")
  public int type() {
    return type;
  }
}
