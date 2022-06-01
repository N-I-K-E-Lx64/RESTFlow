package com.restflow.core.WorkflowParser;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkflowConversionService {

	private final ParserTempState parserTempState;
	private final ParameterFactory parameterFactory;
	private final TaskFactory taskFactory;

	@Autowired
	public WorkflowConversionService(ParserTempState parserTempState, ParameterFactory parameterFactory,
	                                 TaskFactory taskFactory) {
		this.parserTempState = parserTempState;
		this.parameterFactory = parameterFactory;
		this.taskFactory = taskFactory;
	}

	public IWorkflow convertWorkflowModel(@NonNull final WorkflowModel model) {
		IWorkflow convertedWorkflow = new CWorkflow(model.getId(), model.getDescription());

		Map<String, IVariable<?>> variables = model.getVariables()
				.stream()
				.map(variable -> this.parameterFactory.createVariable(variable.name(), parameterFactory.determineClass(variable.type())))
				.collect(Collectors.toMap(IVariable::id, Function.identity()));

		this.parserTempState.setModelIdReference(model.getId());
		this.parserTempState.setVariableReferences(variables);

		Queue<ITask> executionOrder = model.getTasks()
				.stream()
				.map(this.taskFactory::convertTask)
				.collect(Collectors.toCollection(ConcurrentLinkedQueue::new));


		convertedWorkflow.setVariables(variables);
		convertedWorkflow.generateExecutionOrder(executionOrder);

		return convertedWorkflow;
	}
}
