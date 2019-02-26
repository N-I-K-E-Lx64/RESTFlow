package com.example.demo.WorkflowExecution.Objects;

public class CWorkflowExecutionException extends RuntimeException {

    public CWorkflowExecutionException(String message) {
        super(message);
    }

    public CWorkflowExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
