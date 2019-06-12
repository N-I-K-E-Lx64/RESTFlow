package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;

import java.text.MessageFormat;

//TODO: Change this to ENUM!
public class CParameterFactory {
    private static CParameterFactory ourInstance = new CParameterFactory();

    private CParameterFactory() {
    }

    public static CParameterFactory getInstance() {
        return ourInstance;
    }

    /**
     * Creates an object based on given parameters.
     *
     * @param pParameterType  The Type of the Parameter (String, Int, etc.) used for generify the IParameter Object
     * @param pParameterName  Name of the Parameter
     * @param isUserparameter Is true if the parameter value, must be entered by a user.
     * @return an IParameter-Object
     */
    //TODO : Fix the Warnings!
    public IParameter createParameter(String pParameterType, String pParameterName, Boolean isUserparameter) {
        switch (pParameterType.toUpperCase()) {
            case "STRING":
                IParameter<String> lParameterString = new CParameter<String>(pParameterName, isUserparameter);
                return lParameterString;

            case "INT":
                IParameter<Integer> lParameterInteger = new CParameter<Integer>(pParameterName, isUserparameter);
                return lParameterInteger;

            case "DOUBLE":
                IParameter<Double> lParameterDouble = new CParameter<Double>(pParameterName, isUserparameter);
                return lParameterDouble;

            default:
                throw new CWorkflowParseException(MessageFormat.format("Parameter-Type [{0}] doesn't match known types!", pParameterType));
        }
    }

    public Object createParameterValue(String pParameterType, String pParameterValue) {
        switch (pParameterType.toUpperCase()) {
            case "STRING":
                return pParameterValue;

            case "INT":
                return Integer.parseInt(pParameterValue);

            case "DOUBLE":
                return Double.parseDouble(pParameterValue);

            default:
                throw new RuntimeException(MessageFormat.format("Parameter-Type [{0}] doesn't match known types!", pParameterType));
        }
    }
}
