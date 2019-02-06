package com.example.demo.WorkflowParser.WorkflowObjects;

public class CParameter<T> {

    private T value;

    public CParameter(T value) {
        this.value = value;
    }

    public CParameter() {
    }

    public T getValue() {
        return this.value;
    }
}
