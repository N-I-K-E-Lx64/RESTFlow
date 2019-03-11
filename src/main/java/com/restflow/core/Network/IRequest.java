package com.restflow.core.Network;

import org.springframework.lang.NonNull;

public interface IRequest {

    @NonNull
    String url();

    @NonNull
    ERequestType type();

    @NonNull
    String fieldsAsJson();
}
