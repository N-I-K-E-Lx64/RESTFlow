package com.example.demo.WorkflowParser.WorkflowObjects;

public class CParameter<T> implements IParameter {

    private T value;

    public CParameter(T value) {
        this.value = value;
    }
}
