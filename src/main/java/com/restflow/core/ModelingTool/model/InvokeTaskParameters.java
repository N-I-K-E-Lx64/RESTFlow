package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(value = {"isUserParameter"})
public record InvokeTaskParameters(@JsonGetter("inputType") int inputType,
                                   @JsonGetter("raml") String raml,
                                   @JsonGetter("resource") String resource,
                                   @JsonGetter("userParams") List<UserParameter> userParameters,
                                   @JsonGetter("inputVariable") String inputVariable,
                                   @JsonGetter("targetVariable") String targetVariable,
                                   boolean isUserParameter) implements ITaskParameters {

  @Override
  public Object raw() {
    return this;
  }
}