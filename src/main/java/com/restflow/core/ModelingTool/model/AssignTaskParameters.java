package com.restflow.core.ModelingTool.model;

public class AssignTaskParameters implements ITaskParameters {

  private final String parameterId;
  private final String value;
  private final String targetVariable;

  public AssignTaskParameters(String parameterId, String value, String targetVariable) {
    this.parameterId = parameterId;
    this.value = value;
    this.targetVariable = targetVariable;
  }

  public String parameterId() {
    return parameterId;
  }

  public String value() {
    return value;
  }

  public String targetVariable() {
    return targetVariable;
  }

  @Override
  public Object raw() {
    return this;
  }
}
