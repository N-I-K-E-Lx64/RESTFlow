package com.restflow.core.ModelingTool.model;

import java.util.List;
import java.util.UUID;

public class WorkflowModel {

  public UUID id;
  public String name;
  public String description;
  public List<Variable> variables;
  public List<Element> elements;
  public List<Connector> connectors;
  public List<Task> tasks;

  public WorkflowModel(String id, String name, String description,
      List<Variable> variables, List<Element> elements,
      List<Connector> connectors, List<Task> tasks) {
    this.id = UUID.fromString(id);
    this.name = name;
    this.description = description;
    this.variables = variables;
    this.elements = elements;
    this.connectors = connectors;
    this.tasks = tasks;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public List<Element> getElements() {
    return elements;
  }

  public List<Connector> getConnectors() {
    return connectors;
  }

  public List<Task> getTasks() {
    return tasks;
  }
}