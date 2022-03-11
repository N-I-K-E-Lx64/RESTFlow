package com.restflow.core.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.ModelingTool.EModels;
import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.Storage.StorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ModelingToolController {

	private static final Logger logger = LogManager.getLogger(ModelingToolController.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private final StorageService storageService;

	@Autowired
	public ModelingToolController(StorageService storageService) {
		this.storageService = storageService;
	}

	@RequestMapping(value = "/models", method = RequestMethod.GET)
	public ResponseEntity<List<WorkflowModel>> getModels() {
		return ResponseEntity.ok(EModels.INSTANCE.get());
	}

	/**
	 * Returns the model matching the provided id.
	 *
	 * @param id Id of the model
	 * @return Corresponding workflow model
	 * @throws IOException
	 * @see WorkflowModel
	 */
	@RequestMapping(value = "/model/{id:.+}", method = RequestMethod.GET)
	public ResponseEntity<WorkflowModel> getModelById(@PathVariable String id) throws IOException {
		WorkflowModel model = EModels.INSTANCE.apply(UUID.fromString(id));

		// If the model is not loaded currently check if it exists in the storage and load it
		if (Objects.isNull(model)) {
			Resource storedModel = storageService.loadModelAsResource(id);
			model = mapper.readValue(storedModel.getFile(), WorkflowModel.class);

			EModels.INSTANCE.addModel(model);
		}

		return ResponseEntity.ok(model);
	}

	/**
	 * Adds the provided workflow model to the state and saves it as a JSON-file.
	 *
	 * @param model Model that is added to the state
	 * @return The provided workflow model
	 * @see WorkflowModel
	 */
	@RequestMapping(value = "/model", method = RequestMethod.POST)
	public ResponseEntity<WorkflowModel> addModel(@RequestBody WorkflowModel model) throws JsonProcessingException {
		EModels.INSTANCE.addModel(model);
		String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
		storageService.storeModel(prettyJson, String.valueOf(model.id));

		return ResponseEntity.ok(model);
	}
}
