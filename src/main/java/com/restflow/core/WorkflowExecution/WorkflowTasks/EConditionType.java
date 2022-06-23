package com.restflow.core.WorkflowExecution.WorkflowTasks;

import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.NonNull;

public enum EConditionType {

  LESS(0),
  GREATER(1),
  GREATER_OR_EQUALS(2),
  LESS_OR_EQUALS(3),
  EQUALS(4),
  NOT_EQUALS(5),
  STRING_CONTAINS(6),
  NOT_STRING_CONTAINS(7);

  private final int type;

  EConditionType(int type) {
    this.type = type;
  }

  private static final Map<Integer, EConditionType> mapping = new HashMap<>();

  static {
    for (EConditionType conditionType : EConditionType.values()) {
      mapping.put(conditionType.type, conditionType);
    }
  }

  public static EConditionType castIntToEnum(@NonNull final int type) {
    return mapping.get(type);
  }

  /*public EConditionType conditionType(String pType) {

    return switch (pType.toUpperCase()) {
      case "LESS" -> EConditionType.LESS;
      case "GREATER" -> EConditionType.GREATER;
      case "GREATER_OR_EQUALS" -> GREATER_OR_EQUALS;
      case "LESS_OR_EQUALS" -> LESS_OR_EQUALS;
      case "EQUALS" -> EConditionType.EQUALS;
      case "NOT_EQUALS" -> EConditionType.NOT_EQUALS;
      case "CONTAINS" -> EConditionType.STRING_CONTAINS;
      case "NOT_CONTAINS" -> EConditionType.NOT_STRING_CONTAINS;
      default -> throw new CWorkflowParseException(
          MessageFormat.format("Condition type [{0}] unknown!", pType));
    };
  }*/
}
