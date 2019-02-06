package com.example.demo;

import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public enum EWorkflowStorage implements IWorkflowStorage, Function<String, IWorkflow> {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(EWorkflowStorage.class);

    private Map<String, IWorkflow> mWorkflows = new ConcurrentHashMap<>();

    @Override
    public void add(IWorkflow pWorkflow) {
        if (mWorkflows.containsKey(pWorkflow.title()))
            throw new RuntimeException(MessageFormat.format("Workflow [{0}] ist schon vorhanden", pWorkflow.title()));

        mWorkflows.put(pWorkflow.title(), pWorkflow);

        logger.info("Saved Workflow: " + pWorkflow.title());
    }

    @Override
    public void remove(String pWorkflowTitle) {
        mWorkflows.remove(pWorkflowTitle);
    }

    @Override
    public IWorkflow apply(String pWorkflowTitle) {
        final IWorkflow lWorkflow = mWorkflows.get(pWorkflowTitle);

        if (Objects.isNull(lWorkflow)) {
            throw new RuntimeException(MessageFormat.format("Workflow mit dem Title [{0}] nicht gefunden", pWorkflowTitle));
        }
        return lWorkflow;
    }
}
