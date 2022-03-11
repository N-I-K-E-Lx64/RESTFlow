package com.restflow.core.ModelingTool.model;

import java.util.UUID;

public class WorkflowModel {
	public UUID id;
	public String name;
	public String description;
	public Element[] elements;
	public Connector[] connectors;
	public Task[] tasks;

	public String getName() {
		return name;
	}
}


