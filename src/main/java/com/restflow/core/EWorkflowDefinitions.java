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

public enum EWorkflowDefinitions implements IWorkflowDefinitions, Supplier<Set<String>>, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowDefinitions.class);

    private final Map<String, IWorkflow> mWorkflowDefinitions = new ConcurrentHashMap<>();

    private final Map<String, Queue<ITask>> mTaskDefinitions = new ConcurrentHashMap<>();

    @Override
    public void add(@NonNull final IWorkflow pWorkflow) {

        if (mWorkflowDefinitions.containsKey(pWorkflow.definition()))
            throw new RuntimeException(MessageFormat.format("Workflow Definition [{0}] already exists", pWorkflow.definition()));

        mWorkflowDefinitions.put(pWorkflow.definition(), pWorkflow);

        logger.info("Saved Workflow Model for: " + pWorkflow.definition());
    }

    @Override
    public void remove(@NonNull final String pWorkflow) {

        mWorkflowDefinitions.remove(pWorkflow);

        logger.info(MessageFormat.format("The following Workflow Definition [{0}] has been deleted1", pWorkflow));
    }

    @Override
    public void addExecutionOrder(@NonNull final Queue<ITask> pTasks, @NonNull final String pWorkflow) {

        if (mTaskDefinitions.containsKey(pWorkflow))
            throw new RuntimeException(
                    MessageFormat.format("Task Definition of [{0}] already exists", pWorkflow));

        mTaskDefinitions.put(pWorkflow, pTasks);

        logger.info("Saved Task Definition for: " + pWorkflow);
    }

    @Override
    public IWorkflow apply(final String pWorkflowModel) {

        final IWorkflow lWorkflow = mWorkflowDefinitions.get(pWorkflowModel);
        if (Objects.isNull(lWorkflow))
            throw new RuntimeException(
                    MessageFormat.format("Workflow Definition [{0}] could not be found.", pWorkflowModel));

        final Queue<ITask> lExecutionQueue = mTaskDefinitions.get(pWorkflowModel);
        if (Objects.isNull(lExecutionQueue))
            throw new RuntimeException(
                    MessageFormat.format("The execution queue matching [{0}] could not be found", pWorkflowModel));

        return new CWorkflow(lWorkflow, lExecutionQueue);
    }

    @Override
    public Set<String> get() {
        return mWorkflowDefinitions.keySet();
    }
}
