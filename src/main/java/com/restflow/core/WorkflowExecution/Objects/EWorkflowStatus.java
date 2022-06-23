package com.restflow.core.WorkflowExecution.Objects;

public enum EWorkflowStatus {

  INITIATED,
  ACTIVE,
  SUSPENDED,
  COMPLETE,
  TERMINATED,
  STOPPED;

  /**
   * Liefert einen passenden String zum gegebenen Status
   *
   * @return String, welcher den passenden Status beschreibt
   */
  public String get() {
    return switch (this) {
      case INITIATED -> "Initiated";
      case ACTIVE -> "Working";
      case SUSPENDED -> "Waiting";
      case COMPLETE -> "Finished";
      case TERMINATED -> "Failure";
      case STOPPED -> "Stopped";
    };
  }
}
