package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.restflow.core.ModelingTool.model.Parameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.text.MessageFormat;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ParameterFactory {

  /**
   * Creates a generic parameter based on the provided type.
   *
   * @param pParameterId     ID to identify the parameter
   * @param pIsUserParameter Boolean that describes whether the value of this parameter must be set
   *                         by a user.
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
  public <T> Class<?> determineClass(@NonNull final int pType) {
    return switch (pType) {
      case 0 -> String.class;
      case 1 -> Double.class;
      case 2 -> Integer.class;
      case 3 -> JsonNode.class;
      default -> throw new CWorkflowParseException(
          MessageFormat.format("Parameter-Type [{0}] does not match any known type!", pType));
    };
  }

  /**
   * Creates a CParameter-Object out of a Parameter model
   *
   * @param pParameter Model of a Parameter
   * @return A generic IParameter object
   * @see CParameter
   */
  public IParameter<?> createParameterFromParameter(@NonNull final Parameter pParameter) {
    final Class<?> parameterType = this.determineClass(pParameter.type());
    return new CParameter<>(UUID.randomUUID().toString(), false, parameterType).setValue(
        pParameter.value());
  }
}
