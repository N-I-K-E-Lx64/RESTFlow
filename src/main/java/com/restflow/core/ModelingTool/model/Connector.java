package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.util.UUID;

public record Connector(@JsonGetter("id") UUID id, @JsonGetter("points") int[] points,
                        @JsonGetter("source") UUID source, @JsonGetter("target") UUID target) {

}
