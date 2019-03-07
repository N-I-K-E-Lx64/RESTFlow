package com.restflow.core.WorkflowExecution.Objects;

public class CConditionException extends RuntimeException {

    public CConditionException(String message) {
        super(message);
    }

    public CConditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
