package com.example.demo.Network;

public enum ERequestType {

    GET,
    POST,
    DEFAULT,
    INSTANCE;

    public ERequestType get(String pRequestType) {
        switch (pRequestType.toUpperCase()) {
            case "GET":
                return ERequestType.GET;

            case "POST":
                return ERequestType.POST;

            default:
                return ERequestType.DEFAULT;
        }
    }
}
