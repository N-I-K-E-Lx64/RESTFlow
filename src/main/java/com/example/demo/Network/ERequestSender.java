package com.example.demo.Network;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;

public enum ERequestSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERequestSender.class);

    private static final MediaType JSON = MediaType.get("application/json");

    private final OkHttpClient mClient = new OkHttpClient();

    public Response buildRequest(IRequest pRequest) {

        Request.Builder builder = new Request.Builder();
        builder.url(pRequest.url());

        switch (pRequest.type()) {
            case GET:
                builder.get();
                break;

            case POST:
                String lJson = pRequest.fieldsAsJson();
                if (!lJson.equals("")) {
                    logger.info(lJson);
                    RequestBody lBody = RequestBody.create(JSON, lJson);
                    builder.post(lBody);
                }
                break;

            default:
                throw new RuntimeException(MessageFormat.format("Unknown Http-Type: [{0}]!", pRequest.type()));

        }

        Request lRequest = builder.build();

        try {
            return mClient.newCall(lRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
