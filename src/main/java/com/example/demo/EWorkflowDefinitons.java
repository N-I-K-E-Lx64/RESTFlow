package com.example.demo;

import com.example.demo.WorkflowExecution.Objects.CWorkflow;
import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EWorkflowDefinitons implements IWorkflowDefinitions, Supplier<Set<String>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowDefinitons.class);

    private final Map<String, IWorkflow> mDefinitions = new ConcurrentHashMap<>();

    private final Map<String, Queue<ITask>> mTaskDefinitions = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public IWorkflowDefinitions add(@NonNull final IWorkflow pWorkflow) {

        if (mDefinitions.containsKey(pWorkflow.title()))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] existiert schon", pWorkflow.title()));

        mDefinitions.put(pWorkflow.title(), pWorkflow);

        logger.info("Saved Workflow Definition for: " + pWorkflow.title());

        return this;
    }

    @Override
    public IWorkflowDefinitions remove(IWorkflow pWorkflow) {

        mDefinitions.remove(pWorkflow.title());

        return this;
    }

    @Override
    public void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow) {

        if (mTaskDefinitions.containsKey(pWorkflow))
            throw new RuntimeException(MessageFormat.format("Task Definition of Workflow [{0}] already exists", pWorkflow));

        mTaskDefinitions.put(pWorkflow, pTasks);

        logger.info("Saved Task Definition for: " + pWorkflow);
    }

    @Override
    public IWorkflow apply(final String pWorkflowTitle) {

        final IWorkflow lWorkflow = mDefinitions.get(pWorkflowTitle);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] could not be found.", pWorkflowTitle));

        return new CWorkflow(lWorkflow, mTaskDefinitions.get(pWorkflowTitle));
    }

    @Override
    public Set<String> get() {
        return mDefinitions.keySet();
    }
}
