package com.restflow.core.Network.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CRequest implements IRequest {

    private final String mBaseUrl;
    private final String mResourceUrl;
    private final HttpMethod mRequestType;
    private final MediaType mRequestMediaType;
    private final MediaType mResponseMediaType;
    private final Map<String, IParameter> mFields;

    private String mResponse;

    public CRequest(@NonNull final String pUrl, @NonNull final String pResourceUrl,
                    @NonNull HttpMethod pRequestType, @NonNull MediaType pRequestMediaType,
                    @NonNull MediaType mResponseMediaType, @NonNull Map<String, IParameter> pFields) {

        this.mBaseUrl = pUrl;
        this.mResourceUrl = pResourceUrl;
        this.mRequestType = pRequestType;
        this.mRequestMediaType = pRequestMediaType;
        this.mResponseMediaType = mResponseMediaType;
        this.mFields = pFields;
    }

    public void setResponse(@NonNull final String pResponse) {
        this.mResponse = pResponse;
    }

    @NonNull
    @Override
    public String baseUrl() {
        return mBaseUrl;
    }

    @NonNull
    @Override
    public String resourceUrl() {
        return mResourceUrl;
    }

    @NonNull
    @Override
    public HttpMethod type() {
        return mRequestType;
    }

    @NonNull
    @Override
    public MediaType requestMediaType() {
        return mRequestMediaType;
    }

    @Override
    public MediaType responseMediaType() {
        return mResponseMediaType;
    }

    @NonNull
    @Override
    public Map<String, IParameter> fields() {
        return mFields;
    }

    @NonNull
    @Override
    public String response() {
        return mResponse;
    }

    @NonNull
    public String fieldsAsJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> lSerializedFields = new HashMap<>();

        Consumer<Map.Entry<String, IParameter>> serialize = parameter -> {
            if (parameter instanceof IVariable) {
                lSerializedFields.put(parameter.getKey(), ((IVariable) parameter.getValue().value()).value().toString());
            } else {
                lSerializedFields.put(parameter.getKey(), parameter.getValue().value());
            }
        };

        mFields.entrySet().forEach(serialize);
        return mapper.writeValueAsString(lSerializedFields);
    }
}