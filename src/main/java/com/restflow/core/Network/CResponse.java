package com.restflow.core.Network;

import org.springframework.lang.NonNull;

public class CResponse implements IResponse {

    private final String mMediaType;
    private final String mRespone;

    public CResponse(@NonNull final String pMediaType, @NonNull final String pRespone) {
        this.mMediaType = pMediaType;
        this.mRespone = pRespone;
    }

    @NonNull
    @Override
    public String mediaType() {
        return mMediaType;
    }

    @NonNull
    @Override
    public String response() {
        return mRespone;
    }
}
