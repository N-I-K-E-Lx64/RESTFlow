package com.restflow.core.ModelingTool.model;

public class InvokeTaskParameters implements ITaskParameters {

  private final String raml;
  private final String resource;
  private String userParameterId;
  private int userParameterType;
  private String inputVariable;
  private String targetVariable;
  private final boolean isUserParameter;

  public InvokeTaskParameters(String raml, String resource, boolean isUserParameter) {
    this.raml = raml;
    this.resource = resource;
    this.isUserParameter = isUserParameter;
  }

  public String raml() {
    return raml;
  }

  public String resource() {
    return resource;
  }

  public String userParameterId() {
    return userParameterId;
  }

  public int userParameterType() {
    return userParameterType;
  }

  public String inputVariable() {
    return inputVariable;
  }

  public String targetVariable() {
    return targetVariable;
  }

  public boolean isUserParameter() {
    return isUserParameter;
  }

  public void setUserParameterId(String userParameterId) {
    this.userParameterId = userParameterId;
  }

  public void setUserParameterType(int userParameterType) {
    this.userParameterType = userParameterType;
  }

  public void setInputVariable(String inputVariable) {
    this.inputVariable = inputVariable;
  }

  public void setTargetVariable(String targetVariable) {
    this.targetVariable = targetVariable;
  }

  @Override
  public Object raw() {
    return this;
  }
}
