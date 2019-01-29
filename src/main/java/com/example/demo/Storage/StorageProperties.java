package com.example.demo.Storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String mLocation;

    public String location() {
        return mLocation;
    }

    public void setLocation(String pLocation) {
        this.mLocation = pLocation;
    }
}
