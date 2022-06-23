package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Element(@JsonProperty("id") UUID id, @JsonProperty("text") String text,
                      @JsonProperty("x") int x, @JsonProperty("y") int y,
                      @JsonProperty("width") int width, @JsonProperty("height") int height,
                      @JsonProperty("type") int type,
                      @JsonProperty("connectors") ElementConnectors connectors) {

}
