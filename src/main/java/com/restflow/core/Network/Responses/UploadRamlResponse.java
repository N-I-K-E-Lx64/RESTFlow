package com.restflow.core.Network.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.lang.NonNull;

public record UploadRamlResponse(@JsonProperty("fileName") String fileName,
                                 @JsonProperty("resources") List<String> resources) {

  public UploadRamlResponse(@NonNull final String fileName, @NonNull final List<String> resources) {
    this.fileName = fileName;
    this.resources = resources;
  }

  @Override
  public String fileName() {
    return fileName;
  }

  @Override
  public List<String> resources() {
    return resources;
  }
}
