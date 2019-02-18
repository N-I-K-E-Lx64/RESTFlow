package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;

import java.util.Map;
import java.util.Queue;

public class CWorkflowBuilder {

    private String mTitle;
    private String mDescription;
    private Queue<ITaskAction> mExecution;
    private Map<String, IVariable> mVariables;

    public CWorkflowBuilder(String pTitle, String pDescription) {
        mTitle = pTitle;
        mDescription = pDescription;
    }

    public void setVariables(Map<String, IVariable> pVariables) {
        mVariables = pVariables;
    }

    public void setExecution(Queue<ITaskAction> pExecution) {
        mExecution = pExecution;
    }

    public CWorkflow build() {
        return new CWorkflow(mTitle, mDescription, mExecution);
    }
}
