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

public enum ERunningWorkflows implements IRunningWorkflows, Supplier<Set<String>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERunningWorkflows.class);

    private final Map<String, IWorkflow> mWorkflows = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public IWorkflow add(@NonNull final IWorkflow pWorkflow) {

        if (mWorkflows.containsKey(pWorkflow.name()))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] existiert schon", pWorkflow));

        mWorkflows.put(pWorkflow.name(), pWorkflow);

        logger.info("Saved Workflow: " + pWorkflow.name());

        return pWorkflow;
    }

    @NonNull
    @Override
    public IRunningWorkflows remove(@NonNull final String pWorkflow) {

        mWorkflows.remove(pWorkflow);

        return this;
    }

    @Override
    public IWorkflow apply(final String pWorkflowTitle) {

        final IWorkflow lWorkflow = mWorkflows.get(pWorkflowTitle);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(MessageFormat.format("Workflow mit dem Namen [{0}] nicht gefunden", pWorkflowTitle));

        return lWorkflow;
    }

    @Override
    public Set<String> get() {
        return mWorkflows.keySet();
    }
}
