package com.restflow.core.Network;

import java.text.MessageFormat;
import org.springframework.http.HttpMethod;

public enum ERequestTypeBuilder {

  INSTANCE;

  public HttpMethod createHttpMethodFromString(String pRequestType) {
    switch (pRequestType.toUpperCase()) {
      case "GET":
        return HttpMethod.GET;

      case "POST":
        return HttpMethod.POST;

      case "DELETE":
        return HttpMethod.DELETE;

      default:
        throw new RuntimeException(
            MessageFormat.format("Request Type [{0}] doesn't exist.", pRequestType));
    }
  }
}
