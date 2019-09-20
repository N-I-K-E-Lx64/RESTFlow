package com.restflow.core.WorkflowExecution.Objects;

public enum EWorkflowStatus {

    INITIATED,
    ACTIVE,
    SUSPENDED,
    COMPLETE,
    TERMINATED;

    /**
     * Liefert einen passenden String zum gegebenen Status
     *
     * @return String, welcher den passenden Status beschreibt
     */
    public String get() {
        switch (this) {
            //TODO: Better decsriptions!
            case INITIATED:
                return "Initiated";

            case ACTIVE:
                return "Working";

            case SUSPENDED:
                return "Waiting";

            case COMPLETE:
                return "Finished";

            case TERMINATED:
                return "Failure";

            default:
                throw new RuntimeException("Status does not exist!");
        }
    }
}
