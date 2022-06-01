package com.restflow.core.WorkflowExecution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ExecutionLogger {

  INSTANCE;

  private static final Logger logger = LogManager.getLogger(ExecutionLogger.class);

  public void info(String workflowInstance, String message) {
    logger.info(workflowInstance + " reported the following log message: " + message);
  }
}
