package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import org.springframework.lang.NonNull;

public interface IVariable {

    void setValue(final Object pValue);

    @NonNull
    Object value();

    @NonNull
    String name();
}
