package com.restflow.core.Network;

import com.restflow.core.WorkflowExecution.Objects.IWorkflow;

public class CWebClientResponseException extends Exception {

    private final IWorkflow workflow;

    public CWebClientResponseException(IWorkflow workflow, String message) {
        super(message);
        this.workflow = workflow;
    }

    public IWorkflow workflow() {
        return workflow;
    }
}
