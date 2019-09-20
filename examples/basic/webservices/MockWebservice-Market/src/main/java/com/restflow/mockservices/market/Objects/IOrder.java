package com.restflow.mockservices.market.Objects;

public interface IOrder {

    Material material();

    Order material(Material var1);

    long quantity();

    Order quantity(long var1);

    String suppliernumber();

    Order suppliernumber(String var1);
}
