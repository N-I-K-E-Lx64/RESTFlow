package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import org.springframework.lang.NonNull;

public class CStringVariable extends AVariable {

    private String mValue;

    public CStringVariable(@NonNull final String name) {
        super(name, EVariableType.STRING);
    }

    @Override
    public void setValue(String value) {
        this.mValue = value;
    }

    @Override
    public Object value() {
        return mValue;
    }
}
