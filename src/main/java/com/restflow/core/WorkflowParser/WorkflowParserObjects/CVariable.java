package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restflow.core.RESTflowApplication;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowParser.CConversionService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.lang.NonNull;

@Configurable
public class CVariable<T> implements IVariable<T> {

  private final Class<T> mType;
  private final String mVariableId;
  private T mValue;

  protected CConversionService conversionService;

  /**
   * CTor for creating a generic variable
   *
   * @param variableId ID to identify the variable
   * @param type       Representation of the parameter type
   */
  public CVariable(@NonNull final String variableId,
      @NonNull final Class<T> type) {
    this.mVariableId = variableId;
    this.mType = type;
    this.mValue = null;
    this.conversionService = RESTflowApplication.CGlobal.instance()
        .context()
        .getBean(CConversionService.class);
  }

  @NonNull
  @Override
  public String id() {
    return mVariableId;
  }

  @NonNull
  @Override
  public Class<T> type() {
    return mType;
  }

  @Override
  public T value() {
    return mValue;
  }

  @Override
  public void setValue(String value) {
    try {
      // When a workflow is restarted, the value is reset to null
      if (Objects.nonNull(value)) {
        mValue = conversionService.convertStringValue(value, mType);
      } else {
        mValue = null;
      }
    } catch (JsonProcessingException ex) {
      throw new CWorkflowExecutionException("Json string cannot be processed!");
    }
  }
}
