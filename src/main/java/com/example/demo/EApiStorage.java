package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public enum EApiStorage implements IApiStorage, Function<String, Api> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EApiStorage.class);

    private Map<String, Api> apis = new ConcurrentHashMap<>();

    @Override
    public void add(@NonNull Api api) {
        if (apis.containsKey(api.title().value()))
            throw new RuntimeException(MessageFormat.format("Api [{0}] wurde schon abstrahiert", api.title().value()));


        logger.info("Added API: " + api.title().value() + " to the map!");
    }

    @Override
    public void clearStorage() {
        apis.clear();

        logger.info("Cleared Map!");
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
