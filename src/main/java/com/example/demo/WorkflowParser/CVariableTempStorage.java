package com.example.demo.WorkflowParser;

import com.example.demo.WorkflowParser.WorkflowParserObjects.IVariable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CVariableTempStorage {

    private static CVariableTempStorage ourInstance = new CVariableTempStorage();

    private AtomicReference<Map<String, IVariable>> mVariableReference = new AtomicReference<>();

    private CVariableTempStorage() {
    }

    public static CVariableTempStorage getInstance() {
        return ourInstance;
    }

    public void setReference(Map<String, IVariable> pReference) {
        mVariableReference.set(pReference);
    }

    public Map<String, IVariable> reference() {
        return mVariableReference.get();
    }
}
