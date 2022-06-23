package com.restflow.core.WorkflowExecution.Objects;

public class CModelException extends RuntimeException {

  public CModelException(String message) {
    super(message);
  }

  public CModelException(String message, Throwable cause) {
    super(message, cause);
  }

}
