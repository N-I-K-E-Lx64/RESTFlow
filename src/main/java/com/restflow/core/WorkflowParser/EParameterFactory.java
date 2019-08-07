package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;

import java.text.MessageFormat;

public enum EParameterFactory {

    INSTANCE;

    /**
     * Creates an object based on given parameters.
     *
     * @param pParameterType The Type of the Parameter (String, Int, etc.) used for generify the IParameter Object
     * @param pParameterType
     * @param isUserparameter Is true if the parameter value, must be entered by a user.
     * @return an IParameter-Object
     */
    public IParameter createParameter(String pParameterName, String pParameterType, Boolean isUserparameter) {
        switch (pParameterType.toUpperCase()) {
            case "STRING":
                return new CParameter<String>(pParameterName, isUserparameter);

            case "INTEGER":
                return new CParameter<Integer>(pParameterName, isUserparameter);

            case "DOUBLE":
                return new CParameter<Double>(pParameterName, isUserparameter);

            default:
                throw new CWorkflowParseException(MessageFormat.format("Parameter-Type [{0}] doesn't match known types!", pParameterName));
        }
    }

    public IParameter createParameterWithValue(String pParameterName, String pParameterType, String pParameterValue) {
        return createParameter(pParameterName, pParameterType, false)
                .setValue(parseParameterValue(pParameterValue, pParameterType));
    }

    public Object parseParameterValue(String pParameterValue, String pParameterType) {
            switch (pParameterType.toUpperCase()) {
                case "STRING":
                    return pParameterValue;

                case "INTEGER":
                    return Integer.parseInt(pParameterValue);

                case "Double":
                    return Double.parseDouble(pParameterValue);

                default:
                    throw new CWorkflowParseException(MessageFormat.format("Parameter-Type [{0}] doesn't match known types!", pParameterType));
            }
    }
}
