package com.example.demo;

import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public enum EApiStorage implements IApiStorage, Function<String, Api> {

    INSTANCE;

    private Map<String, Api> apis = new ConcurrentHashMap<>();


    @Override
    public void add(@NonNull Api api) {
        if (apis.containsKey(api.title().value()))
            throw new RuntimeException(MessageFormat.format("Api [{0}] wurde schon abstrahiert", api.title().value()));
    }

    @NonNull
    @Override
    public Api apply(String title) {
        final Api api = apis.get(title);
        if (Objects.isNull(api))
            throw new RuntimeException(MessageFormat.format("Api mit dem Titel [{0}] wurde nicht gefunden", title));

        return api;
    }
}
