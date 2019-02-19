package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskFactory;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private final String mTitle;
    private final String mDescription;

    private final Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private Map<String, IVariable> mVariables;

    public CWorkflow(String pTitle, String pDescription, Map<String, IVariable> pVariables) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
        this.mVariables = Collections.synchronizedMap(pVariables);
    }

    @NonNull
    @Override
    public String name() {
        return mTitle;
    }

    @Override
    public Queue<ITaskAction> getQueue() {
        return mExecution;
    }

    @Override
    public void generateExecutionOrder(Queue<ITask> pTasks) {
        for (ITask lTask : pTasks) {
            mExecution.add(EWorkflowTaskFactory.INSTANCE.factory(this, lTask));
        }
    }
}
