package com.restflow.core.ModelingTool.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.util.List;
import java.util.UUID;

public record WorkflowModel(@JsonGetter("id") UUID id,
                            @JsonGetter("name") String name,
                            @JsonGetter("description") String description,
                            @JsonGetter("variables") List<Variable> variables,
                            @JsonGetter("elements") List<Element> elements,
                            @JsonGetter("connectors") List<Connector> connectors,
                            @JsonGetter("tasks") List<Task> tasks) {

}