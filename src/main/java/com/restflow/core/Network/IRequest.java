package com.restflow.core.Network;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;

import java.util.Map;

public interface IRequest {

    @NonNull
    String baseUrl();

    @NonNull
    String resourceUrl();

    @NonNull
    HttpMethod type();

    @NonNull
    Map fields();
}
