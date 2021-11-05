package com.restflow.core.WorkflowParser;

import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class ParserTaskDefinitionTempStorage implements Function<String, ITask> {
    private final Map<String, ITask> taskDefinitions = new ConcurrentHashMap<>();

    public void reset() {
        taskDefinitions.clear();
    }

    public void setTaskDefinition(@NonNull final ITask reference) {
        if (this.taskDefinitions.containsKey(reference.id())) {
            throw new CWorkflowParseException(MessageFormat.format("Task definition with id [{0}] already exists", reference.id()));
        } else {
            taskDefinitions.put(reference.id(), reference);
        }
    }

    @NonNull
    @Override
    public ITask apply(String taskId) {
        if (this.taskDefinitions.containsKey(taskId)) {
            return taskDefinitions.get(taskId);
        } else {
            throw new CWorkflowParseException(MessageFormat.format("Task with id [{0}] could not be found", taskId));
        }
    }
}
