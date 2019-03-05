package com.example.demo.Network;

import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IVariable;
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

        try {
            Map<String, Object> lSerializedFields = new HashMap<>();
            //TODO : Change this to stream
            for (Map.Entry<String, IParameter> entry : mFields.entrySet()) {
                if (entry.getValue().value() instanceof IVariable) {

                    if (mFields.size() == 1) {
                        return ((IVariable) entry.getValue().value()).value().toString();
                    }
                    lSerializedFields.put(entry.getKey(), ((IVariable) entry.getValue().value()).value().toString());
                } else {
                    lSerializedFields.put(entry.getKey(), entry.getValue().value());
                }
            }

            return mapper.writeValueAsString(lSerializedFields);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }
}
