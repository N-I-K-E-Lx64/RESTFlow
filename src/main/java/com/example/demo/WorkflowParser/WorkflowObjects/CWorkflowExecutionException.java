package com.example.demo.WorkflowParser.WorkflowObjects;

public class CWorkflowExecutionException extends RuntimeException {

    public CWorkflowExecutionException(String message) {
        super(message);
    }

    public CWorkflowExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
