package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface IParameter<T> {

    @NonNull
    String id();

    @NonNull
    T value();

    IParameter<T> setValue(String pValue);
}
