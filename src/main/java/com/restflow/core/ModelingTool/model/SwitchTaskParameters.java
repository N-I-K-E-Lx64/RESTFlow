package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record SwitchTaskParameters(@JsonProperty("condition") Condition condition,
                                   @JsonProperty("trueFlow") UUID taskIdTrue,
                                   @JsonProperty("falseFlow") UUID taskIdFalse)
    implements ITaskParameters {

  @Override
  public Object raw() {
    return this;
  }
}
