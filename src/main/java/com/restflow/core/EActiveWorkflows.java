package com.restflow.core;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
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

public enum EActiveWorkflows implements IActiveWorkflows, Supplier<Set<Map.Entry<String, IWorkflow>>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EActiveWorkflows.class);

    private final Map<String, IWorkflow> mWorkflows = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public IWorkflow add(@NonNull final String pWorkflowName, @NonNull final IWorkflow pWorkflow) {

        if (mWorkflows.containsKey(pWorkflowName))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] existiert schon", pWorkflow));

        mWorkflows.put(pWorkflowName, pWorkflow);

        logger.info("Saved Workflow: " + pWorkflowName);

        return pWorkflow;
    }

    @NonNull
    @Override
    public void remove(@NonNull final String pWorkflowName) {

        mWorkflows.remove(pWorkflowName);

        logger.info("Removed Workflow: " + pWorkflowName);
    }

    @Override
    public IWorkflow apply(final String pWorkflowTitle) {

        final IWorkflow lWorkflow = mWorkflows.get(pWorkflowTitle);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] could not be found!", pWorkflowTitle));

        return lWorkflow;
    }

    @Override
    public Set<Map.Entry<String, IWorkflow>> get() {
        return mWorkflows.entrySet();
    }
}
