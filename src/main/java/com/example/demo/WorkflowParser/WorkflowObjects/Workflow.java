package com.example.demo.WorkflowParser.WorkflowObjects;

import java.util.Map;
import java.util.Queue;

public class Workflow {

    private final String title;
    private final String description;
    private Map<String, String> variables;
    private Queue<ITask> tasks;

    public Workflow(String title, String description, Map<String, String> variables, Queue<ITask> tasks) {
        this.title = title;
        this.description = description;
        this.variables = variables;
        this.tasks = tasks;
    }
}
