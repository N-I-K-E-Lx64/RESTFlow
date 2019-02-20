package com.example.demo.WorkflowParser.WorkflowParserObjects;

import java.util.concurrent.atomic.AtomicReference;

public class CAssignTask {

    private final AtomicReference<IVariable> mVariableReference;

    //TODO : Filter-Möglichkeiten implementieren!

    public CAssignTask(IVariable pVariableReference) {
        mVariableReference = new AtomicReference<IVariable>(pVariableReference);
    }
}
