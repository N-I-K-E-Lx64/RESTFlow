package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

//TODO : Change this to ENUM
public class CVariableTempStorage implements Function<String, IVariable> {

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
            throw new RuntimeException(MessageFormat.format("Variable [{0}] could not be found!", lNormalizedName));

        return lVariable;
    }
}
