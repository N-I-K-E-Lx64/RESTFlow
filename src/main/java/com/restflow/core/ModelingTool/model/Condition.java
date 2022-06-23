package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Condition(@JsonProperty("type") int conditionType,
                        @JsonProperty("isVariable1") boolean isVariable1,
                        @JsonProperty("isVariable2") boolean isVariable2,
                        @JsonProperty("var1") String variable1,
                        @JsonProperty("var2") String variable2,
                        @JsonProperty("param1") Parameter parameter1,
                        @JsonProperty("param2") Parameter parameter2) {

}
