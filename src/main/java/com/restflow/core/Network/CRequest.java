package com.restflow.core.Network;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;

import java.util.Map;

public class CRequest implements IRequest {

    private final String mBaseUrl;
    private final String mResourceUrl;
    private final HttpMethod mRequestType;
    private final Map mFields;

    public CRequest(@NonNull final String pUrl, @NonNull final String pResourceUrl,
                    @NonNull HttpMethod pRequestType, @NonNull Map<String, IParameter> pFields) {
        this.mBaseUrl = pUrl;
        this.mResourceUrl = pResourceUrl;
        this.mRequestType = pRequestType;
        this.mFields = pFields;
    }

    /*
    TODO: Alles auf LinkedMultiValueMap umbauen!
    TODO: Parser so anpassen, dass er mit mehreren Parametern bezogen auf den selben key umgehen kann.
     */

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
    public Map fields() {
        return mFields;
    }

    /*@NonNull
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
    }*/
}
