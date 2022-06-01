package com.restflow.core.WorkflowExecution.Objects;

public class CUserInteractionException extends RuntimeException {

  public CUserInteractionException(String message) {
    super(message);
  }

  public CUserInteractionException(String message, Throwable cause) {
    super(message, cause);
  }

}
