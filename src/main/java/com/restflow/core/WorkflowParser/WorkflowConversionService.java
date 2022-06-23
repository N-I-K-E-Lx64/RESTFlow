package com.restflow.core.WorkflowParser;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class WorkflowConversionService implements Function<UUID, Queue<ITask>> {

  private final ParserTempState parserTempState;
  private final ParameterFactory parameterFactory;
  private final TaskFactory taskFactory;

  private final Map<UUID, Queue<ITask>> taskDefinitions;

  @Autowired
  public WorkflowConversionService(ParserTempState parserTempState,
      ParameterFactory parameterFactory, TaskFactory taskFactory) {
    this.parserTempState = parserTempState;
    this.parameterFactory = parameterFactory;
    this.taskFactory = taskFactory;

    this.taskDefinitions = new ConcurrentHashMap<>();
  }

  public IWorkflow convertWorkflowModel(@NonNull final WorkflowModel model) {
    IWorkflow convertedWorkflow = new CWorkflow(model.id(), model.description());

    Map<String, IVariable<?>> variables = model.variables()
        .stream()
        .map(variable -> this.parameterFactory.createVariable(variable.name(),
            parameterFactory.determineClass(variable.type())))
        .collect(Collectors.toMap(IVariable::id, Function.identity()));

    this.parserTempState.setModelIdReference(model.id());
    this.parserTempState.setVariableReferences(variables);

    Queue<ITask> executionOrder = this.taskFactory.generateExecutionOrder(model);

    this.taskDefinitions.put(model.id(), executionOrder);

    convertedWorkflow.setVariables(variables);
    convertedWorkflow.generateExecutionOrder(executionOrder);

    return convertedWorkflow;
  }

  @Override
  public Queue<ITask> apply(UUID modelId) {
    if (this.taskDefinitions.containsKey(modelId)) {
      return this.taskDefinitions.get(modelId);
    }
    return null;
  }


}
