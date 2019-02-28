package com.example.demo.WorkflowExecution.Objects;

public enum EWorkflowStatus {

    WORKING,
    WAITING,
    ERROR;

    /**
     * Liefert einen passenden String zum gegebenen Status
     *
     * @return String, welcher den passenden Status beschreibt
     */
    public String get() {
        switch (this) {
            case WORKING:
                return "Working";

            case WAITING:
                return "Waiting";

            case ERROR:
                return "Failure";

            default:
                throw new RuntimeException("Status does not exist!");
        }
    }
}
