package com.example.demo;

import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

public interface IApiStorage {

    /**
     * Add a new API Object in the Storage
     *
     * @param api Api Object
     */
    void add(@NonNull final Api api);

    /**
     * Delete all Apis in the Map
     */
    void clearStorage();
}
