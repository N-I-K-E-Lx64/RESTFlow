package com.restflow.core.Network;

import java.util.function.Supplier;

public interface IMessage extends Supplier<Object> {

    String workflow();

    String parameterName();
}
