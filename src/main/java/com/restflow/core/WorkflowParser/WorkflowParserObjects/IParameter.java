package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface IParameter<T> {

    @NonNull
    String name();

    @NonNull
    T value();

    void setValue(T pValue);
}
