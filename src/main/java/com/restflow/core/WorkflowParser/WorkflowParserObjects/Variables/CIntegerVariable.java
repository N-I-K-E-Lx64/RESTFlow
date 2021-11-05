package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import org.springframework.lang.NonNull;

public class CIntegerVariable extends AVariable {

    private Integer mValue;

    public CIntegerVariable(@NonNull final String name) {
        super(name, EVariableType.INTEGER);
    }

    @Override
    public void setValue(String value) {
        this.mValue = Integer.parseInt(value);
    }

    @Override
    public Object value() {
        return mValue;
    }
}
