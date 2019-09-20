package com.restflow.mockservices.order.Objects;

public interface IMaterial {

    String number();

    Material number(String var1);

    String description();

    Material description(String var1);

    double price();

    Material price(double var1);

    String currency();

    Material currency(String var1);

    String type();

    Material type(String var1);

    Material plant(String var1);

    String plant();

    String purchaseorg();

    Material purchaseorg(String var1);

    String purchasegroup();

    Material purchasegroup(String var1);

    String vendor();

    Material vendor(String var1);
}
