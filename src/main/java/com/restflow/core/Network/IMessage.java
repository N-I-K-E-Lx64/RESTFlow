package com.restflow.core.Network;

import java.util.function.Supplier;

public interface IMessage extends Supplier<String> {

    String workflow();

    String parameterName();

    Object parameterValue();
}
