package com.example.demo.WorkflowParser;

import com.example.demo.WorkflowParser.WorkflowParserObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;

public class CParameterFactory {
    private static CParameterFactory ourInstance = new CParameterFactory();

    private CParameterFactory() {
    }

    public static CParameterFactory getInstance() {
        return ourInstance;
    }

    //TODO - Check if it Works!
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
                throw new WorkflowParseException("Parameter-Type doesn't match known types!");
        }
    }
}
