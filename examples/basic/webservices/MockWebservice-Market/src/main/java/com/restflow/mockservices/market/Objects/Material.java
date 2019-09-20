package com.restflow.mockservices.market.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Material implements IMaterial {

    private String number;
    private String description;
    private double price;
    private String currency;
    private String type;
    private String plant;
    private String purchaseorg;
    private String purchasegroup;
    private String vendor;

    public Material(String p_number, String p_description) {
        this.number = p_number;
        this.description = p_description;
    }

    public String number() {
        return this.number;
    }

    public Material number(String p_number) {
        this.number = p_number;
        return this;
    }

    public String description() {
        return this.description;
    }

    public Material description(String p_description) {
        this.description = p_description;
        return this;
    }

    public double price() {
        return this.price;
    }

    public Material price(double p_price) {
        this.price = p_price;
        return this;
    }

    public String currency() {
        return this.currency;
    }

    public Material currency(String p_currency) {
        this.currency = p_currency;
        return this;
    }

    public String type() {
        return this.type;
    }

    public Material type(String p_type) {
        this.type = p_type;
        return this;
    }

    public Material plant(String p_plant) {
        this.plant = p_plant;
        return this;
    }

    public String plant() {
        return this.plant;
    }

    public Material purchaseorg(String p_purchaseorg) {
        this.purchaseorg = p_purchaseorg;
        return this;
    }

    public String purchaseorg() {
        return this.purchaseorg;
    }

    public Material purchasegroup(String p_purchasegroup) {
        this.purchasegroup = p_purchasegroup;
        return this;
    }

    public String purchasegroup() {
        return this.purchasegroup;
    }

    public Material vendor(String p_vendor) {
        this.vendor = p_vendor;
        return this;
    }

    public String vendor() {
        return this.vendor;
    }

}
