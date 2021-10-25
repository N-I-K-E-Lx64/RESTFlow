package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;

public enum EParameterFactory {

    INSTANCE;

    /**
     * Creates a generic parameter based on the provided type.
     *
     * @param pParameterId     ID to identify the parameter
     * @param pIsUserParameter Boolean that describes whether the value of this parameter must be set by a user.
     * @param pType            Representation of the parameter type
     * @param <T>              Generic
     * @return A generic Parameter
     */
    public <T> IParameter<T> createParameter(@NonNull final String pParameterId,
                                             @NonNull final Boolean pIsUserParameter,
                                             Class<T> pType) {
        return new CParameter<>(pParameterId, pIsUserParameter, pType);
    }

    /**
     * Determines the correct java class for the respective parameter
     *
     * @param pType String representation of the parameter Type
     * @param <T>   Generic
     * @return The class of all the supported parameter types
     */
    public <T> Class<?> determineClass(@NonNull final String pType) {
        switch (pType.toUpperCase()) {
            case "STRING":
                return String.class;

            case "DOUBLE":
                return Double.class;

            default:
                throw new CWorkflowParseException(MessageFormat.format("Parameter-Type [{0}] does not match any known type!", pType));
        }
    }
}
