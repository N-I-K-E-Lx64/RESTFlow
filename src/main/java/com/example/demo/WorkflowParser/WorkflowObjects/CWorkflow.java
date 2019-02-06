package com.example.demo.WorkflowParser.WorkflowObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private final String mTitle;
    private final String mDescription;

    private Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private Map<String, JsonNode> variables = Collections.synchronizedMap(new HashMap<>());

    public CWorkflow(String pTitle, String pDescription) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
    }

    @Override
    public String title() {
        return mTitle;
    }

    @Override
    public Queue<ITaskAction> getQueue() {
        return mExecution;
    }

    @Override
    public void setQueue(Queue<ITaskAction> pExecution) {
        this.mExecution = pExecution;
    }
}
