package com.restflow.core.Network;

import org.springframework.lang.NonNull;

public interface IResponse {

    @NonNull
    String mediaType();

    @NonNull
    String response();
}
