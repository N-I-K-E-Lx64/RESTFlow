package com.restflow.core.WorkflowDatabase;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EActiveWorkflows implements IActiveWorkflows, Supplier<Set<IWorkflow>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EActiveWorkflows.class);

    private final Map<String, IWorkflow> mWorkflows = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public IWorkflow add(@NonNull final String pInstanceName, @NonNull final IWorkflow pWorkflow) {

        pWorkflow.setInstanceName(pInstanceName);

        if (mWorkflows.containsKey(pInstanceName))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] existiert schon", pWorkflow));

        mWorkflows.put(pInstanceName, pWorkflow);

        logger.info("Saved Workflow: " + pInstanceName);

        return pWorkflow;
    }

    @NonNull
    @Override
    public IWorkflow restart(@NonNull final String pWorkflowInstance) {

        IWorkflow lDeletedWorkflow = mWorkflows.get(pWorkflowInstance);
        IWorkflow lCopiedWorkflowDefinition = EWorkflowDefinitions.INSTANCE.apply(lDeletedWorkflow.definition());

        lCopiedWorkflowDefinition.setInstanceName(lDeletedWorkflow.instance());

        mWorkflows.replace(pWorkflowInstance, lDeletedWorkflow, lCopiedWorkflowDefinition);

        return lCopiedWorkflowDefinition;
    }

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
    public Set<IWorkflow> get() {
        return new HashSet<IWorkflow>(mWorkflows.values());
    }
}
