package com.example.demo.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface IParameter<T> {

    @NonNull
    String name();

    @NonNull
    T value();

    void setValue(@NonNull T pValue);
}
