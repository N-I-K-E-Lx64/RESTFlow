package com.restflow.core.Network;

import org.springframework.lang.NonNull;

public class CResponse implements IResponse {

    private final String mMediaType;
    private final String mResponse;

    public CResponse(@NonNull final String pMediaType, @NonNull final String pResponse) {
        this.mMediaType = pMediaType;
        this.mResponse = pResponse;
    }

    @NonNull
    @Override
    public String mediaType() {
        return mMediaType;
    }

    @NonNull
    @Override
    public String response() {
        return mResponse;
    }
}
