package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface ICondition {

  @NonNull
  Boolean execute();
}
