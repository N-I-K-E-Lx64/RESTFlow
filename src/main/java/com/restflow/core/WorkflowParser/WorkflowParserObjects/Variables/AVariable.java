package com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import org.springframework.lang.NonNull;

public abstract class AVariable {

    protected String mName;
    protected EVariableType mVariableType;

    protected AVariable(@NonNull final String name,
                        @NonNull final EVariableType variableType) {
        this.mName = name;
        this.mVariableType = variableType;
    }

    public abstract void setValue(String value);

    public abstract Object value();

    @NonNull
    public String name() {
        return mName;
    }

    @NonNull
    public EVariableType variableType() {
        return mVariableType;
    }

}
