package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface IVariable<T> {

  @NonNull
  String id();

  @NonNull
  Class<T> type();

  T value();

  void setValue(String value);
}
