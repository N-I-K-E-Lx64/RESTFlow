package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class Connector {

  private final UUID id;
  private final int[] points;
  private final UUID source;
  private final UUID target;

  @JsonCreator
  public Connector(@JsonProperty("id") UUID id, @JsonProperty("points") int[] points,
      @JsonProperty("source") UUID source, @JsonProperty("target") UUID target) {
    this.id = id;
    this.points = points;
    this.source = source;
    this.target = target;
  }

  @JsonGetter("id")
  public UUID id() {
    return id;
  }

  @JsonGetter("points")
  public int[] points() {
    return points;
  }

  @JsonGetter("source")
  public UUID source() {
    return source;
  }

  @JsonGetter("target")
  public UUID target() {
    return target;
  }
}
