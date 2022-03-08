package com.restflow.core.Network.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    MediaType requestMediaType();

    @NonNull
    MediaType responseMediaType();

    @NonNull
    Map<String, IParameter<?>> fields();

    void setResponse(@NonNull final String pResponse);

    @NonNull
    String response();

    @NonNull
    String fieldsAsJson() throws JsonProcessingException;
}
