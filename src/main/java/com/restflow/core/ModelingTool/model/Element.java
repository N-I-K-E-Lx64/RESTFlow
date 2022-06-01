package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class Element {

  private final UUID id;
  private final String text;
  private final int x;
  private final int y;
  private final int width;
  private final int height;
  private final int type;
  private final String[] connectors;

  @JsonCreator
  public Element(@JsonProperty("id") UUID id, @JsonProperty("test") String text,
      @JsonProperty("x") int x,
      @JsonProperty("y") int y, @JsonProperty("width") int width,
      @JsonProperty("height") int height,
      @JsonProperty("type") int type, @JsonProperty("connectors") String[] connectors) {
    this.id = id;
    this.text = text;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.type = type;
    this.connectors = connectors;
  }

  @JsonGetter("id")
  public UUID id() {
    return id;
  }

  @JsonGetter("text")
  public String text() {
    return text;
  }

  @JsonGetter("x")
  public int x() {
    return x;
  }

  @JsonGetter("y")
  public int y() {
    return y;
  }

  @JsonGetter("width")
  public int width() {
    return width;
  }

  @JsonGetter("height")
  public int height() {
    return height;
  }

  @JsonGetter("type")
  public int type() {
    return type;
  }

  @JsonGetter("connectors")
  public String[] connectors() {
    return connectors;
  }
}
