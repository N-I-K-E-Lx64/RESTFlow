package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class ParameterFactory {

    /**
     * Creates a generic parameter based on the provided type.
     *
     * @param pParameterId     ID to identify the parameter
     * @param pIsUserParameter Boolean that describes whether the value of this parameter must be set by a user.
     * @param pType            Representation of the parameter type
     * @param <T>              Generic
     * @return A generic IParameter object
     * @see CParameter
     */
    public <T> IParameter<T> createParameter(@NonNull final String pParameterId,
                                             @NonNull final Boolean pIsUserParameter,
                                             @NonNull final Class<T> pType) {
        return new CParameter<>(pParameterId, pIsUserParameter, pType);
    }

    /**
     * Creates a generic parameter with a const value based on the provided type.
     *
     * @param pParameterId ID to identify the parameter
     * @param pType        Representation of the parameter type
     * @param pValue       Value of the const parameter (as string)
     * @param <T>          Generic
     * @return A generic IParameter object
     * @see CParameter
     */
    public <T> IParameter<T> createConstParameter(@NonNull final String pParameterId,
                                                  @NonNull final Class<T> pType,
                                                  @NonNull final String pValue) {
        return new CParameter<>(pParameterId, false, pType).setValue(pValue);
    }

    public <T> IVariable<T> createVariable(@NonNull final String pVariableId,
                                           @NonNull final Class<T> pType) {
        return new CVariable<>(pVariableId, pType);
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

            case "INTEGER":
                return Integer.class;

            case "JSON":
                return JsonNode.class;

            default:
                throw new CWorkflowParseException(MessageFormat.format("Parameter-Type [{0}] does not match any known type!", pType));
        }
    }
}
