package com.restflow.core.ModelingTool.model;

public class Task {

  public String id;
  public String title;
  public String description;
  public int type;
  public ITaskParameters params;

  public Task(String id, String title, String description, int type, ITaskParameters params) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.type = type;
    this.params = params;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getType() {
    return type;
  }

  public ITaskParameters getParams() {
    return params;
  }
}
