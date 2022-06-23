package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.RESTflowApplication;
import com.restflow.core.WorkflowExecution.Objects.CConditionException;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EConditionType;
import com.restflow.core.WorkflowParser.CConversionService;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.lang.NonNull;

public class CCondition implements ICondition {

  private final EConditionType mConditionType;
  private final AtomicReference<IParameter<?>> mFirstParameter = new AtomicReference<>();
  private final AtomicReference<IParameter<?>> mSecondParameter = new AtomicReference<>();

  protected CConversionService conversionService;

  public CCondition(@NonNull final EConditionType pConditionType, IParameter<?> pFirstParameter,
      IParameter<?> pSecondParameter) {
    this.mConditionType = pConditionType;
    this.mFirstParameter.set(pFirstParameter);
    this.mSecondParameter.set(pSecondParameter);
    this.conversionService = RESTflowApplication.CGlobal.instance().context()
        .getBean(CConversionService.class);
  }

  @NonNull
  @Override
  public Boolean execute() {

    if (Objects.isNull(mFirstParameter.get()) || Objects.isNull(mSecondParameter.get())) {
      throw new CConditionException("No decision could be made due to a zero value!" +
          " First Parameter: " + mFirstParameter.get().id() + " = " + Objects.isNull(
          mFirstParameter.get()) +
          " Second Parameter: " + mSecondParameter.get().id() + " = " + Objects.isNull(
          mSecondParameter.get()));
    }

    return switch (mConditionType) {
      case LESS -> convertParameter(mFirstParameter.get(), Double.class) < convertParameter(
          mSecondParameter.get(), Double.class);
      case GREATER -> convertParameter(mFirstParameter.get(), Double.class) > convertParameter(
          mSecondParameter.get(), Double.class);
      case GREATER_OR_EQUALS ->
          convertParameter(mFirstParameter.get(), Double.class) >= convertParameter(
              mSecondParameter.get(), Double.class);
      case LESS_OR_EQUALS ->
          convertParameter(mFirstParameter.get(), Double.class) <= convertParameter(
              mSecondParameter.get(), Double.class);
      case EQUALS -> Objects.equals(mFirstParameter.get(), mSecondParameter.get());
      case NOT_EQUALS -> !Objects.equals(mFirstParameter.get(), mSecondParameter.get());
      case STRING_CONTAINS -> convertParameter(mFirstParameter.get(), String.class).contains(
          convertParameter(mSecondParameter.get(), String.class));
      case NOT_STRING_CONTAINS -> !(convertParameter(mFirstParameter.get(), String.class).contains(
          convertParameter(mSecondParameter.get(), String.class)));
    };
  }

  private <T> T convertParameter(@NonNull final IParameter<?> parameter,
      @NonNull final Class<T> type) {
    return this.conversionService.convertValue(parameter.value(), type);
  }
}
