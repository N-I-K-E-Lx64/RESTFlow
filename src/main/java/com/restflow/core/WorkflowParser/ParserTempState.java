package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Service
public class ParserTempState implements Function<String, IVariable<?>> {

    private final AtomicReference<Map<String, IVariable<?>>> variableReferences = new AtomicReference<>();

    private final AtomicReference<UUID> modelIdReference = new AtomicReference<>();

    public void setVariableReferences(@NonNull final Map<String, IVariable<?>> references) {
        this.variableReferences.set(references);
    }

    public void setModelIdReference(@NonNull final UUID modelId) {
        this.modelIdReference.set(modelId);
    }

    @NonNull
    @Override
    public IVariable<?> apply(String variableName) {
        if (variableReferences.get().containsKey(variableName)) {
            return variableReferences.get().get(variableName);
        } else {
            throw new CWorkflowParseException(MessageFormat.format("Variable [{0}] could not be found", variableName));
        }
    }

    public String modelId() {
        return modelIdReference.get().toString();
    }
}
