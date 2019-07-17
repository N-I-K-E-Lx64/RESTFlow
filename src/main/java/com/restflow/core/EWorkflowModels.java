package com.restflow.core;

import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
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

public enum EWorkflowModels implements IWorkflowDefinitions, Supplier<Set<String>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowModels.class);

    private final Map<String, IWorkflow> mModels = new ConcurrentHashMap<>();

    private final Map<String, Queue<ITask>> mTaskDefinitions = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public void add(@NonNull final IWorkflow pWorkflow) {

        if (mModels.containsKey(pWorkflow.model()))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] already exists", pWorkflow.model()));

        mModels.put(pWorkflow.model() + "-MODEL", pWorkflow);

        logger.info("Saved Workflow Model for: " + pWorkflow.model());
    }

    @NonNull
    @Override
    public void remove(@NonNull final String pWorkflow) {

        mModels.remove(pWorkflow);

        logger.info(MessageFormat.format("The following Workflow Model [{0}] has been deleted1", pWorkflow));
    }

    @Override
    public void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow) {

        String lWorkflowModelName = pWorkflow + "-MODEL";

        if (mTaskDefinitions.containsKey(lWorkflowModelName))
            throw new RuntimeException(
                    MessageFormat.format("Task Definition of [{0}] already exists", lWorkflowModelName));

        mTaskDefinitions.put(lWorkflowModelName, pTasks);

        logger.info("Saved Task Definition for: " + lWorkflowModelName);
    }

    @Override
    public IWorkflow apply(final String pWorkflowModel) {

        final IWorkflow lWorkflow = mModels.get(pWorkflowModel);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(
                    MessageFormat.format("Workflow [{0}] could not be found.", pWorkflowModel));

        final Queue<ITask> lExecutionQueue = mTaskDefinitions.get(pWorkflowModel);
        if (Objects.isNull(lExecutionQueue))
            throw new RuntimeException(
                    MessageFormat.format("The execution queue matching [{0}] could not be found", pWorkflowModel));

        return new CWorkflow(lWorkflow, lExecutionQueue);
    }

    @Override
    public Set<String> get() {
        return mModels.keySet();
    }
}
