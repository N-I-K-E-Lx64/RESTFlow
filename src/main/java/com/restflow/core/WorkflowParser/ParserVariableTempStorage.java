package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Service
public class ParserVariableTempStorage implements Function<String, IVariable<?>> {

    private final AtomicReference<Map<String, IVariable<?>>> variableReferences = new AtomicReference<>();

    public void setVariableReferences(@NonNull final Map<String, IVariable<?>> references) {
        this.variableReferences.set(references);
    }

    @NonNull
    @Override
    public IVariable<?> apply(String variableName) {
        String lNormalizedName;
        if (variableName.contains("VARIABLES")) {
            lNormalizedName = variableName.split("\\.")[1];
        } else {
            lNormalizedName = variableName;
        }

        if (variableReferences.get().containsKey(lNormalizedName)) {
            return variableReferences.get().get(lNormalizedName);
        } else {
            throw new CWorkflowParseException(MessageFormat.format("Variable [{0}] could not be found", lNormalizedName));
        }
    }
}
