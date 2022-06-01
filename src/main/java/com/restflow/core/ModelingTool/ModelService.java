package com.restflow.core.ModelingTool;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowParser.WorkflowParserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ModelService implements Supplier<List<WorkflowModel>>, Function<UUID, WorkflowModel> {

	private static final Logger logger = LogManager.getLogger(ModelService.class);

	private final Map<UUID, WorkflowModel> workflowModels;

	private final StorageService storageService;
	private final WorkflowParserService workflowParserService;

	@Autowired
	public ModelService(StorageService storageService, WorkflowParserService workflowParserService) {
		this.storageService = storageService;
		this.workflowParserService = workflowParserService;

		this.workflowModels = loadModels();
	}

	/**
	 * Loads all saved models in the state
	 * @return Map of all saved models
	 */
	private Map<UUID, WorkflowModel> loadModels() {
		List<File> models = this.storageService.loadAllModels();
		if (models.size() > 0) {
			logger.info(MessageFormat.format("{0} models were successfully restored", models.size()));
			return models.stream()
					.map(workflowParserService::parseFileModel)
					.collect(Collectors.toMap(WorkflowModel::getId, Function.identity()));
		} else {
			return new ConcurrentHashMap<>();
		}
	}

	/**
	 * Parses the provided model string and either updates an existing model or create a new one
	 * @param jsonModel Model string
	 * @param isUpdate Declares whether the provided model is an update for an existing model
	 * @return parsed Workflow Model
	 */
	public WorkflowModel parseModel(@NonNull final String jsonModel, final boolean isUpdate) {
		// Parse the provided model
		WorkflowModel parsedModel = this.workflowParserService.parseToolModel(jsonModel);
		// Add it to the state and save it permanently
		if (isUpdate) {
			this.updateModel(parsedModel);
			logger.info(MessageFormat.format("Model {0} is successfully updated.", parsedModel.id));
		} else {
			this.addModel(parsedModel);
			logger.info(MessageFormat.format("Saved new model {0}.", parsedModel.id));
		}
		// Store model permanently
		this.storageService.storeModel(jsonModel, String.valueOf(parsedModel.id));

		return parsedModel;
	}

	public void addModel(@NonNull final WorkflowModel model) {
		if (workflowModels.containsKey(model.id)) {
			if (model != workflowModels.get(model.id)) {
				workflowModels.replace(model.id, model);
			} else {
				throw new RuntimeException(MessageFormat.format("Workflow Model with id [{0}] already exists!", model.name));
			}
		} else {
			workflowModels.put(model.id, model);
		}
	}

	public void updateModel(@NonNull final WorkflowModel model) {
		if (workflowModels.containsKey(model.id)) {
			if (model != workflowModels.get(model.id)) {
				workflowModels.replace(model.id, model);
			}
		} else {
			throw new RuntimeException(MessageFormat.format("Workflow Model with id [{0}] could not be found!", model.id));
		}
	}

	public void removeModel(@NonNull final UUID modelId) {
		if (!workflowModels.containsKey(modelId))
			throw new RuntimeException(MessageFormat.format("Workflow Model with id [{0}] could not be found!", modelId));

		workflowModels.remove(modelId);
		// Remove the model also from the storage
		storageService.deleteFile(modelId.toString().concat(".json"));
	}

	/**
	 * Returns the specified workflow model
	 * @param modelId the id of the model
	 * @return The workflow model that matches the specified ID.
	 */
	@Override
	public WorkflowModel apply(UUID modelId) {
		final WorkflowModel model = workflowModels.get(modelId);
		if (Objects.isNull(model))
			return null;

		return model;
	}

	/**
	 * Returns a sorted list of all workflow models. The models are sorted based on their name!
	 * @return Sorted List of workflow models
	 */
	@Override
	public List<WorkflowModel> get() {
		List<WorkflowModel> sortedList = new ArrayList<>(workflowModels.values());
		if (!sortedList.isEmpty()) {
			sortedList.sort(Comparator.comparing(WorkflowModel::getName));
		}

		return sortedList;
	}
}
