package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AssignTaskParameters(@JsonProperty("paramId") String parameterId,
                                   @JsonProperty("value") String value,
                                   @JsonProperty("variable") String targetVariable) implements
    ITaskParameters {

  @Override
  public Object raw() {
    return this;
  }
}
