package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record ElementConnectors(@JsonProperty("incoming") UUID incomingConnectors,
                                @JsonProperty("outgoing") UUID[] outgoingConnectors) {

}
