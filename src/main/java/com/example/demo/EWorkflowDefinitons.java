package com.example.demo;

import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EWorkflowDefinitons implements IWorkflowDefinitions, Supplier<Set<String>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowDefinitons.class);

    private final Map<String, IWorkflow> mDefinitions = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public IWorkflowDefinitions add(@NonNull final IWorkflow pWorkflow) {

        if (mDefinitions.containsKey(pWorkflow.name()))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] existiert schon", pWorkflow.name()));

        mDefinitions.put(pWorkflow.name(), pWorkflow);

        logger.info("Saved Workflow Definition for: " + pWorkflow.name());

        return this;
    }

    @Override
    public IWorkflowDefinitions remove(IWorkflow pWorkflow) {

        mDefinitions.remove(pWorkflow.name());

        return this;
    }

    @Override
    public IWorkflow apply(final String pWorkflowTitle) {

        final IWorkflow lWorkflow = mDefinitions.get(pWorkflowTitle);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] could not be found.", pWorkflowTitle));

        return (IWorkflow) lWorkflow.clone();
    }

    @Override
    public Set<String> get() {
        return mDefinitions.keySet();
    }
}
