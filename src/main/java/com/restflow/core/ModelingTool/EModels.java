package com.restflow.core.ModelingTool;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public enum EModels implements Supplier<List<WorkflowModel>>, Function<UUID, WorkflowModel> {

	INSTANCE;

	private final Map<UUID, WorkflowModel> workflowModels = new ConcurrentHashMap<>();

	// TODO : Split this into a addModel and a updateModel function.
	@NonNull
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
