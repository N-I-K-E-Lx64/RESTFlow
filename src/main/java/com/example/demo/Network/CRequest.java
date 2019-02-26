package com.example.demo.Network;

import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class CRequest implements IRequest {

    private final String mUrl;
    private final ERequestType mRequestType;
    private final Map<String, IParameter> mFields;

    public CRequest(@NonNull String pUrl, @NonNull ERequestType pRequestType, @NonNull Map<String, IParameter> pFields) {
        this.mUrl = pUrl;
        this.mRequestType = pRequestType;
        this.mFields = pFields;
    }

    @NonNull
    @Override
    public String url() {
        return mUrl;
    }

    @NonNull
    @Override
    public ERequestType type() {
        return mRequestType;
    }

    @NonNull
    @Override
    public String fieldsAsJson() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> lSerializedFields = new HashMap<>();
        mFields.forEach((key, value) -> {
            lSerializedFields.put(key, value.value());
        });

        String lJson = "";
        try {
            lJson = mapper.writeValueAsString(lSerializedFields);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return lJson;
    }
}
