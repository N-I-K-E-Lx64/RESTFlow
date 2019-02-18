package com.example.demo.WorkflowParser;

public class WorkflowParseException extends RuntimeException {

    public WorkflowParseException(String message) {
        super(message);
    }

    public WorkflowParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
