package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

public enum EVariableTempStorage implements Function<String, IVariable> {

    INSTANCE;

    private final AtomicReference<Map<String, IVariable>> mVariableReference = new AtomicReference<>();

    EVariableTempStorage() {
    }

    public void setReference(Map<String, IVariable> pReference) {
        mVariableReference.set(pReference);
    }

    public Map<String, IVariable> reference() {
        return mVariableReference.get();
    }

    @NonNull
    @Override
    public IVariable apply(String pVariableName) {

        String lNormalizedName;
        if (pVariableName.contains("VARIABLES")) {
            lNormalizedName = pVariableName.split(Pattern.quote("."))[1];
        } else {
            lNormalizedName = pVariableName;
        }

        final IVariable lVariable = mVariableReference.get().get(lNormalizedName);
        if (Objects.isNull(lVariable))
            throw new CWorkflowParseException(MessageFormat.format("Variable [{0}] could not be found!", lNormalizedName));

        return lVariable;
    }
}
