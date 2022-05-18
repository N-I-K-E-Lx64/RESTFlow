package com.restflow.core.WorkflowParser;

import com.restflow.core.ModelingTool.model.AssignTaskParameters;
import com.restflow.core.ModelingTool.model.InvokeTaskParameters;
import com.restflow.core.ModelingTool.model.Task;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CVariableReference;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
public class TaskFactory {

	private final StorageService storageService;

	private final ParserVariableTempStorage variableTempStorage;

	private final ParameterFactory parameterFactory;

	@Autowired
	public TaskFactory(StorageService storageService, ParserVariableTempStorage variableTempStorage,
	                   ParameterFactory parameterFactory) {
		this.storageService = storageService;
		this.variableTempStorage = variableTempStorage;
		this.parameterFactory = parameterFactory;
	}

	public ITask convertTask(@NonNull final Task task) {
		return switch (task.type) {
			case 0 -> convertInvokeTask(task);
			case 1 -> convertAssignTask(task);
			default -> null;
		};
	}

	private ITask convertInvokeTask(@NonNull final Task invokeTask) {
		InvokeTaskParameters parameters = (InvokeTaskParameters) invokeTask.getParams().raw();

		final Resource ramlResource = storageService.loadAsResource(parameters.raml(), "test");
		final Api api = ERamlParser.INSTANCE.parseRaml(ramlResource);

		if (Objects.isNull(api)) throw new CWorkflowParseException(
				MessageFormat.format("RAML file {0} could not be parsed", ramlResource.getFilename()));

		final int resourceIndex = IntStream.range(0, api.resources().size())
				.filter(index -> api.resources().get(index).relativeUri().value().equals(parameters.resource()))
				.findFirst()
				.orElseThrow(RuntimeException::new);

		Map<String, IParameter<?>> invokeParameters = new HashMap<>();
		if (!parameters.isUserParameter()) {
			invokeParameters.put(
					parameters.inputVariable(),
					new CVariableReference(parameters.inputVariable(), this.variableTempStorage.apply(parameters.inputVariable()))
			);
		} else {
			invokeParameters.put(
					parameters.userParameterId(),
					parameterFactory.createParameter(
							parameters.userParameterId(),
							parameters.isUserParameter(),
							parameterFactory.determineClass(parameters.userParameterType())
					)
			);
		}

		IVariable<?> targetVariable = this.variableTempStorage.apply(parameters.targetVariable());

		return new CInvokeServiceTask(
				invokeTask.getId(),
				invokeTask.getDescription(),
				resourceIndex,
				api,
				invokeParameters,
				targetVariable
		);
	}

	private ITask convertAssignTask(@NonNull final Task assignTask) {
		AssignTaskParameters parameters = (AssignTaskParameters) assignTask.getParams().raw();

		IVariable<?> targetVariable = this.variableTempStorage.apply(parameters.targetVariable());
		IParameter<?> parameter = this.parameterFactory.createConstParameter(
				parameters.parameterId(),
				targetVariable.type(),
				parameters.value()
		);

		return new CAssignTask(assignTask.getId(), assignTask.getDescription(), parameter, targetVariable);
	}
}
