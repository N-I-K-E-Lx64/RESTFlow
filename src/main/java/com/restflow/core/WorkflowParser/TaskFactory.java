package com.restflow.core.WorkflowParser;

import com.restflow.core.ModelingTool.model.AssignTaskParameters;
import com.restflow.core.ModelingTool.model.Condition;
import com.restflow.core.ModelingTool.model.Connector;
import com.restflow.core.ModelingTool.model.Element;
import com.restflow.core.ModelingTool.model.InvokeTaskParameters;
import com.restflow.core.ModelingTool.model.SwitchTaskParameters;
import com.restflow.core.ModelingTool.model.Task;
import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.Objects.CModelException;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EConditionType;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ETaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CCondition;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CVariableReference;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ICondition;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CSwitchTask;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class TaskFactory {

  private final ParserTempState parserTempState;
  private final ParameterFactory parameterFactory;
  private final RamlParserService ramlParserService;

  @Autowired
  public TaskFactory(ParserTempState parserTempState,
      ParameterFactory parameterFactory, RamlParserService ramlParserService) {
    this.parserTempState = parserTempState;
    this.parameterFactory = parameterFactory;
    this.ramlParserService = ramlParserService;
  }

  public ITask convertTask(@NonNull final Task task, @NonNull final WorkflowModel model) {
    return switch (task.type()) {
      case 0 -> convertInvokeTask(task);
      case 1 -> convertAssignTask(task);
      case 2 -> convertSwitchTask(task, model);
      default -> null;
    };
  }

  private ITask convertInvokeTask(@NonNull final Task invokeTask) {
    InvokeTaskParameters parameters = (InvokeTaskParameters) invokeTask.parameters().raw();

    final Api api = this.ramlParserService.apply(this.parserTempState.modelId(), parameters.raml());

    if (Objects.isNull(api)) {
      throw new CWorkflowParseException(
          MessageFormat.format("Parsed RAML file {0} could not be found", parameters.raml()));
    }

    final int resourceIndex = IntStream.range(0, api.resources().size())
        .filter(
            index -> api.resources().get(index).relativeUri().value().equals(parameters.resource()))
        .findFirst()
        .orElseThrow(RuntimeException::new);

    Map<String, IParameter<?>> invokeParameters = new HashMap<>();
    if (!parameters.isUserParameter()) {
      invokeParameters.put(
          parameters.inputVariable(),
          new CVariableReference(parameters.inputVariable(),
              this.parserTempState.apply(parameters.inputVariable()))
      );
    } else {
      parameters.userParameters()
          .forEach(param -> invokeParameters.put(param.id(), this.parameterFactory.createParameter(
              param.id(), true, this.parameterFactory.determineClass(param.type()))));
    }

    IVariable<?> targetVariable = this.parserTempState.apply(parameters.targetVariable());

    return new CInvokeServiceTask(
        invokeTask.id(),
        invokeTask.title(),
        invokeTask.description(),
        resourceIndex,
        api,
        invokeParameters,
        targetVariable
    );
  }

  private ITask convertAssignTask(@NonNull final Task assignTask) {
    AssignTaskParameters parameters = (AssignTaskParameters) assignTask.parameters().raw();

    IVariable<?> targetVariable = this.parserTempState.apply(parameters.targetVariable());
    IParameter<?> parameter = this.parameterFactory.createConstParameter(
        parameters.parameterId(),
        targetVariable.type(),
        parameters.value()
    );

    return new CAssignTask(assignTask.id(), assignTask.title(), assignTask.description(), parameter,
        targetVariable);
  }

  private ITask convertSwitchTask(@NonNull final Task switchTask,
      @NonNull final WorkflowModel model) {
    SwitchTaskParameters parameters = (SwitchTaskParameters) switchTask.parameters().raw();

    final Condition condition = parameters.condition();
    final EConditionType conditionType = EConditionType.castIntToEnum(condition.conditionType());
    IParameter<?> parameter1;
    IParameter<?> parameter2;
    if (condition.isVariable1()) {
      final String variable1 = condition.variable1();
      parameter1 = new CVariableReference(variable1, this.parserTempState.apply(variable1));
    } else {
      parameter1 = this.parameterFactory.createParameterFromParameter(condition.parameter1());
    }

    if (condition.isVariable2()) {
      final String variable2 = condition.variable2();
      parameter2 = new CVariableReference(variable2, this.parserTempState.apply(variable2));
    } else {
      parameter2 = this.parameterFactory.createParameterFromParameter(condition.parameter2());
    }

    ICondition iCondition = new CCondition(conditionType, parameter1, parameter2);

    Queue<ITask> trueFlow = this.generateExecutionOrderFromTask(parameters.taskIdTrue(), model);
    Queue<ITask> falseFlow = this.generateExecutionOrderFromTask(parameters.taskIdFalse(), model);

    return new CSwitchTask(switchTask.id(), switchTask.title(), switchTask.description(),
        iCondition, trueFlow, falseFlow);
  }

  /**
   * Generates the execution order from a model. It starts on the start Element and then moves
   * forward via its connector to the next element (the rest is handled by a loop).
   *
   * @param model The corresponding workflow model
   * @return Linked Queue of ITask objects (execution order)
   */
  public Queue<ITask> generateExecutionOrder(@NonNull final WorkflowModel model) {
    // Find the start element
    Element startElement = model.elements()
        .stream()
        .filter(element -> element.type() == 0)
        .findFirst()
        .orElse(null);

    // Check if element exists
    if (Objects.isNull(startElement)) {
      throw new CModelException(
          MessageFormat.format("Model {0} has no start Element!", model.name()));
    }

    // Get the associated *outgoing* connector
    Connector associatedConnector = model.connectors()
        .stream()
        .filter(connector -> Objects.equals(connector.id(),
            startElement.connectors().outgoingConnectors()[0]))
        .findFirst()
        .orElse(null);

    // Check if the connector exists
    if (Objects.isNull(associatedConnector)) {
      throw new CModelException("Start Element is not connected to anything!");
    }

    // Get the connector target (here: the taskId / elementId)
    UUID firstTaskId = associatedConnector.target();

    return this.generateExecutionOrderFromTask(firstTaskId, model);
  }

  /**
   * Builds the execution order task for task.
   *
   * @param startingTask ID of the first task
   * @param model        The corresponding workflow model
   * @return Linked Queue of ITask objects (execution order)
   */
  public Queue<ITask> generateExecutionOrderFromTask(@NonNull final UUID startingTask,
      @NonNull final WorkflowModel model) {
    Queue<ITask> executionOrder = new ConcurrentLinkedQueue<>();
    UUID taskId = startingTask;

    do {
      // Get the first "task" element
      Element currentElement = this.elementSupplier(model, taskId).get();

      // Checks if this element exists
      if (Objects.isNull(currentElement)) {
        throw new CModelException(
            MessageFormat.format("Specified element {0} does not exist in model {0}!", taskId,
                model.name()));
      }

      // If the element is a end-element -> stop generation
      if (currentElement.type() == 1) {
        break;
      }

      // Get the associated task (mind that the taskID and the elementID are the same)
      // The task is also converted during this process
      ITask convertedTask = this.taskSupplier(model, taskId).get();

      // Check if task exists
      if (Objects.isNull(convertedTask)) {
        throw new CModelException(
            MessageFormat.format("Specified task {0} does not exist in model {0}!", taskId,
                model.name()));
      }

      // Add the task to the execution order
      executionOrder.add(convertedTask);

      // If the task is a switch task the remaining execution order must be determined differently (handled in the TaskFactory)
      if (convertedTask.taskType() == ETaskType.SWITCH) {
        break;
      }

      // Determine the ID of the outgoing connector
      UUID outgoingConnectorId = currentElement.connectors().outgoingConnectors()[0];

      // Get this connector
      Connector outgoingConnector = model.connectors()
          .stream()
          .filter(connector -> Objects.equals(connector.id(), outgoingConnectorId))
          .findFirst()
          .orElse(null);

      // Check if the connector exists
      if (Objects.isNull(outgoingConnector)) {
        throw new CModelException(
            MessageFormat.format("Specified connector {0} does not exist in model {0}!",
                outgoingConnectorId, model.name()));
      }

      // The target of the connector becomes the taskId for the next loop run
      taskId = outgoingConnector.target();
    }
    while (true);

    return executionOrder;
  }

  private Supplier<Element> elementSupplier(@NonNull final WorkflowModel model,
      @NonNull final UUID elementId) {
    return () -> model.elements()
        .stream()
        .filter(element -> Objects.equals(element.id(), elementId))
        .findFirst()
        .orElse(null);
  }

  private Supplier<ITask> taskSupplier(@NonNull final WorkflowModel model,
      @NonNull final UUID taskId) {
    return () -> model.tasks()
        .stream()
        .filter(task -> Objects.equals(task.id(), taskId))
        .map(task -> this.convertTask(task, model))
        .findFirst()
        .orElse(null);
  }
}
