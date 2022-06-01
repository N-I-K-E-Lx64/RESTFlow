package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowParser.CConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.lang.NonNull;

@Configurable
public class CVariable<T> implements IVariable<T> {

  private final Class<T> mType;
  private final String mVariableId;
  private T mValue;
  @Autowired
  private CConversionService conversionService;

  public CVariable(@NonNull final String variableId,
      @NonNull final Class<T> type) {
    this.mVariableId = variableId;
    this.mType = type;
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
    mValue = conversionService.convertValue(value, mType);
  }
}
