package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.ModelingTool.ModelService;
import com.restflow.core.ModelingTool.model.WorkflowModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ModelingToolController {

	private static final Logger logger = LogManager.getLogger(ModelingToolController.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private final ModelService modelService;

	@Autowired
	public ModelingToolController(ModelService modelService) {
		this.modelService = modelService;
	}

	@RequestMapping(value = "/models", method = RequestMethod.GET)
	public ResponseEntity<List<WorkflowModel>> getModels() {
		return ResponseEntity.ok(this.modelService.get());
	}

	/**
	 * Adds the provided workflow model to the state and saves it as a JSON-file.
	 *
	 * @param jsonModel String that represents the workflow model
	 * @return The parsed workflow model
	 * @see WorkflowModel
	 */
	@RequestMapping(value = "/model", method = RequestMethod.PUT)
	public ResponseEntity<WorkflowModel> addModel(@RequestBody String jsonModel) {
		WorkflowModel parsedModel = this.modelService.parseModel(jsonModel, false);

		logger.info(MessageFormat.format("Received a new model with id {0}", parsedModel.id));

		return ResponseEntity.ok(parsedModel);
	}

	/**
	 * Updates an existing model in the state.
	 *
	 * @param jsonModel String that represents the workflow model
	 * @return The parsed workflow model
	 * @see WorkflowModel
	 */
	@RequestMapping(value = "/model", method = RequestMethod.PATCH)
	public ResponseEntity<WorkflowModel> updateModel(@RequestBody String jsonModel) {
		WorkflowModel parsedModel = this.modelService.parseModel(jsonModel, true);

		logger.info(MessageFormat.format("Successfully updated model {0}", parsedModel.id));

		return ResponseEntity.ok(parsedModel);
	}

	/**
	 * Removes the model specified by the provided id.
	 * @param modelId Id of the model that should be removed
	 * @return Success message
	 */
	@RequestMapping(value = "/model/{modelId:.+}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteModel(@PathVariable UUID modelId) {
		this.modelService.removeModel(modelId);

		logger.info(MessageFormat.format("Successfully removed Model {0}", modelId));

		return ResponseEntity.ok("Successfully removed Model: " + modelId);
	}
}
