package com.restflow.mockservices.market.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Order implements IOrder {

    private Material material;
    private String suppliernumber;
    private long quantity;

    public Order() {
    }

    public Order(Material p_material, long p_quantity, String p_suppliernumber) {
        this.material = p_material;
        this.quantity = p_quantity;
        this.suppliernumber = p_suppliernumber;
    }

    public Material material() {
        return this.material;
    }

    public Order material(Material p_material) {
        this.material = p_material;
        return this;
    }

    public long quantity() {
        return this.quantity;
    }

    public Order quantity(long p_quantity) {
        this.quantity = p_quantity;
        return this;
    }

    public String suppliernumber() {
        return this.suppliernumber;
    }

    public Order suppliernumber(String p_suppliernumber) {
        this.suppliernumber = p_suppliernumber;
        return this;
    }
}
