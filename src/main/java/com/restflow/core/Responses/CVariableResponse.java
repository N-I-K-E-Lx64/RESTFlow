package com.restflow.core.Responses;

import com.fasterxml.jackson.databind.JsonNode;

public class CVariableResponse {

    private final String name;
    private final JsonNode value;


    public CVariableResponse(String pName, JsonNode pValue) {
        this.name = pName;
        this.value = pValue;
    }

    public String getName() {
        return name;
    }

    public JsonNode getValue() {
        return value;
    }
}
