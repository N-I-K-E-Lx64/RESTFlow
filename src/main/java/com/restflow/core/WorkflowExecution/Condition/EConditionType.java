package com.restflow.core.WorkflowExecution.Condition;

import com.restflow.core.WorkflowParser.CWorkflowParseException;

import java.text.MessageFormat;

public enum EConditionType {

    SMALLER,
    BIGGER,
    NUMBER_EQUALS,
    NOT_NUMBER_EQUALS,
    STRING_EQUALS,
    NOT_STRING_EQUALS,
    STRING_CONTAINS,
    NOT_STRING_CONTAINS,
    INSTANCE;

    public EConditionType conditionType(String pType) {

        switch (pType) {
            case "SMALLER":
                return EConditionType.SMALLER;

            case "BIGGER":
                return EConditionType.BIGGER;

            case "NUMBER_EQUALS":
                return EConditionType.NUMBER_EQUALS;

            case "NOT_NUMBER_EQUALS":
                return EConditionType.NOT_NUMBER_EQUALS;

            case "EQUALS":
                return EConditionType.STRING_EQUALS;

            case "NOT_EQUALS":
                return EConditionType.NOT_STRING_EQUALS;

            case "CONTAINS":
                return EConditionType.STRING_CONTAINS;

            case "NOT_CONTAINS":
                return EConditionType.NOT_STRING_CONTAINS;

            default:
                throw new CWorkflowParseException(MessageFormat.format("Condition type [{0}] unknown!", pType));
        }
    }
}
