package com.restflow.core.Storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

  // All files uploaded through the REST API will be stored in this directory
  private String mLocation = "upload-dir";

  public String location() {
    return mLocation;
  }

  public void setLocation(String pLocation) {
    this.mLocation = pLocation;
  }
}
