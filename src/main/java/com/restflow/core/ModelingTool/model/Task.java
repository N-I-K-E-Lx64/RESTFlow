package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.util.UUID;

public record Task(@JsonGetter("id") UUID id,
                   @JsonGetter("title") String title,
                   @JsonGetter("description") String description,
                   @JsonGetter("type") int type,
                   @JsonGetter("params") ITaskParameters parameters) {

}