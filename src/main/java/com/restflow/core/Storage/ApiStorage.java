package com.restflow.core.Storage;

import java.util.List;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

public record ApiStorage(String fileName, Api api, List<String> resources) {

  public ApiStorage(@NonNull final String fileName, @NonNull final Api api,
      @NonNull final List<String> resources) {
    this.fileName = fileName;
    this.api = api;
    this.resources = resources;
  }
}
