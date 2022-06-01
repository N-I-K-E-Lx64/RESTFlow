package com.restflow.core.WorkflowExecution.Condition;

import com.restflow.core.WorkflowParser.CWorkflowParseException;
import java.text.MessageFormat;

// TODO : Put this into the Object sub-package
public enum EConditionType {

  SMALLER,
  BIGGER,
  GREATER_OR_EQUALS,
  LESS_OR_EQUALS,
  EQUALS,
  NOT_EQUALS,
  STRING_CONTAINS,
  NOT_STRING_CONTAINS,
  INSTANCE;

  public EConditionType conditionType(String pType) {

    switch (pType.toUpperCase()) {
      case "SMALLER":
        return EConditionType.SMALLER;

      case "BIGGER":
        return EConditionType.BIGGER;

      case "GREATER_OR_EQUALS":
        return GREATER_OR_EQUALS;

      case "LESS_OR_EQUALS":
        return LESS_OR_EQUALS;

      case "EQUALS":
        return EConditionType.EQUALS;

      case "NOT_EQUALS":
        return EConditionType.NOT_EQUALS;

      case "CONTAINS":
        return EConditionType.STRING_CONTAINS;

      case "NOT_CONTAINS":
        return EConditionType.NOT_STRING_CONTAINS;

      default:
        throw new CWorkflowParseException(
            MessageFormat.format("Condition type [{0}] unknown!", pType));
    }
  }
}
