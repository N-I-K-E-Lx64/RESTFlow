package com.restflow.mockservices.market.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OrderRequest {

    private String partNumber;
    private String budget;

    public Double budget() {
        return Double.parseDouble(budget);
    }

    public String partNumber() {
        return partNumber;
    }
}
