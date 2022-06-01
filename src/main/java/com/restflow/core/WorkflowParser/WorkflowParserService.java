package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.ModelingTool.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// TODO : Use a custom deserializer (https://www.baeldung.com/jackson-deserialization)
@Service
public class WorkflowParserService {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowParserService.class);

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Parses the provided model string into a usable workflow model.
	 *
	 * @param model String representation of the model
	 * @return Parsed workflow model
	 * @see WorkflowModel
	 */
	public WorkflowModel parseToolModel(@NonNull final String model) {
		try {
			// Reads the whole JSON tree from the provided model string
			JsonNode rootNode = mapper.readTree(model);
			return parseRootNode(rootNode);
		} catch (JsonProcessingException e) {
			throw new CWorkflowParseException("Could not parse transmitted model");
		}
	}

	/**
	 * Parses the provided Resource into a usable workflow model.
	 *
	 * @param model The model resource (file)
	 * @return The parsed workflow model
	 * @see WorkflowModel
	 */
	public WorkflowModel parseFileModel(@NonNull final File model) {
		try {
			// Read the whole JSON tree from the file and puts it in a JSON node
			JsonNode rootNode = mapper.readTree(model);
			return parseRootNode(rootNode);
		} catch (IOException ex) {
			throw new CWorkflowParseException(MessageFormat.format("Could not parse the model file {0}!", model.getName(), ex));
		}

	}

	private WorkflowModel parseRootNode(@NonNull final JsonNode rootNode) {
		String modelId = rootNode.path("id").asText();
		String modelName = rootNode.path("name").asText();
		String modelDescription = rootNode.path("description").asText();

		List<Variable> variables = parseVariables(rootNode);
		List<Element> elements = parseElements(rootNode);
		List<Connector> connectors = parseConnectors(rootNode);
		List<Task> tasks = parseTasks(rootNode);

		return new WorkflowModel(modelId, modelName, modelDescription, variables, elements, connectors, tasks);
	}

	private List<Variable> parseVariables(@NonNull final JsonNode rootNode) {
		if (rootNode.has("variables") && rootNode.path("variables").isArray()) {
			JsonNode variablesNode = rootNode.path("variables");
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(variablesNode.elements(), Spliterator.ORDERED), false)
					.map(variable -> parseJsonNode(variable, Variable.class)).toList();
		} else {
			throw new CWorkflowParseException("Either there are no elements or the element attribute is not an array!");
		}
	}

	private List<Element> parseElements(@NonNull final JsonNode rootNode) {
		if (rootNode.has("elements") && rootNode.path("elements").isArray()) {
			JsonNode elementsNode = rootNode.path("elements");
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elementsNode.elements(), Spliterator.ORDERED), false)
					.map(element -> parseJsonNode(element, Element.class)).toList();
		} else {
			throw new CWorkflowParseException("Either there are no elements or the element attribute is not an array!");
		}
	}

	private List<Connector> parseConnectors(@NonNull final JsonNode rootNode) {
		if (rootNode.has("connectors") && rootNode.path("connectors").isArray()) {
			JsonNode connectorsNode = rootNode.path("connectors");
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(connectorsNode.elements(), Spliterator.ORDERED), false)
					.map(connector -> parseJsonNode(connector, Connector.class)).toList();
		} else {
			throw new CWorkflowParseException("Either there are no connectors or the connector attribute is not an array!");
		}
	}

	private List<Task> parseTasks(@NonNull final JsonNode rootNode) {
		if (rootNode.has("tasks") && rootNode.path("tasks").isArray()) {
			JsonNode tasksNode = rootNode.path("tasks");
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(tasksNode.elements(), Spliterator.ORDERED), false)
					.map(this::parseTaskNode)
					.collect(Collectors.toCollection(LinkedList::new));
		} else {
			throw new CWorkflowParseException("Either there are no tasks or the task attribute is not an array!");
		}
	}

	private Task parseTaskNode(@NonNull final JsonNode taskNode) {
		String taskId = taskNode.path("id").asText();
		String taskTitle = taskNode.path("title").asText();
		String taskDescription = taskNode.path("description").asText();
		int taskType = taskNode.path("type").asInt();

		ITaskParameters taskParams = parseTaskParamsNode(taskNode, taskType);

		return new Task(taskId, taskTitle, taskDescription, taskType, taskParams);
	}

	private ITaskParameters parseTaskParamsNode(@NonNull final JsonNode taskParamsNode, @NonNull final int taskType) {
		if (taskType == 0 && taskParamsNode.has("invokeParams")) {
			JsonNode paramsNode = taskParamsNode.path("invokeParams");
			String ramlFile = paramsNode.path("raml").asText();
			String ramlResource = paramsNode.path("resource").asText();
			boolean isUserParameter = paramsNode.path("inputType").asInt() != 0;
			InvokeTaskParameters invokeParameters = new InvokeTaskParameters(ramlFile, ramlResource, isUserParameter);

			if (isUserParameter) {
				invokeParameters.setUserParameterId(paramsNode.path("userParamId").asText());
				invokeParameters.setUserParameterType(paramsNode.path("userParamType").asInt());
			} else {
				invokeParameters.setInputVariable(paramsNode.path("inputVariable").asText());
			}

			if (paramsNode.has("targetVariable")) {
				invokeParameters.setTargetVariable(paramsNode.path("targetVariable").asText());
			}

			return invokeParameters;
		} else if (taskType == 1 && taskParamsNode.has("assignParams")) {
			JsonNode paramsNode = taskParamsNode.path("assignParams");
			String parameterId = paramsNode.path("paramId").asText();
			String parameterValue = paramsNode.path("value").asText();
			String targetVariable = paramsNode.path("variable").asText();

			return new AssignTaskParameters(parameterId, parameterValue, targetVariable);
		} else {
			throw new CWorkflowParseException("The provided task type is not supported!");
		}
	}

	/**
	 * Wrapper for the Jackson treeToValue Function, so it can be used in a stream map function while be capable of
	 * catching exceptions!
	 *
	 * @param node Json Node
	 * @param type The class in which the JsonNode should be parsed
	 * @param <T>  Generic indicator
	 * @return The parsed object / list
	 */
	private <T> T parseJsonNode(@NonNull final JsonNode node, @NonNull final Class<T> type) {
		try {
			return mapper.treeToValue(node, type);
		} catch (JsonProcessingException ex) {
			throw new CWorkflowParseException(ex.getMessage());
		}
	}
}
