package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.ModelingTool.ModelService;
import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.WorkflowService;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ModelingToolController {

  private static final Logger logger = LogManager.getLogger(ModelingToolController.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  private final ModelService modelService;
  private final WorkflowService workflowService;

  @Autowired
  public ModelingToolController(ModelService modelService, WorkflowService workflowService) {
    this.modelService = modelService;
    this.workflowService = workflowService;
  }

  @RequestMapping(value = "/models", method = RequestMethod.GET)
  public ResponseEntity<List<WorkflowModel>> getModels() {
    return ResponseEntity.ok(this.modelService.get());
  }

  /**
   * Returns the specified model
   *
   * @param modelId Id of the model
   * @return The specified workflow model
   * @see WorkflowModel
   */
  @RequestMapping(value = "/model/{modelId:.+}", method = RequestMethod.GET)
  public ResponseEntity<WorkflowModel> getModelById(@PathVariable UUID modelId) {
    return ResponseEntity.ok(this.modelService.apply(modelId));
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
   *
   * @param modelId Id of the model that should be removed
   * @return Success message
   */
  @RequestMapping(value = "/model/{modelId:.+}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteModel(@PathVariable UUID modelId) {
    this.modelService.removeModel(modelId);

    logger.info(MessageFormat.format("Successfully removed Model {0}", modelId));

    return ResponseEntity.ok("Successfully removed Model: " + modelId);
  }

  /**
   * Function for starting / creating a workflow instance
   *
   * @param modelId    Id of the model on which the new instance is based on.
   * @param instanceId Name of the instance
   * @return suitable response
   */
  @RequestMapping(value = "/execute/{modelId:.+}/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<?> startModelExecution(@PathVariable UUID modelId,
      @PathVariable String instanceId) {
    this.workflowService.accept(this.modelService.apply(modelId), instanceId);

    return ResponseEntity.ok(
        MessageFormat.format("Successfully created a new instance {0} from the model {1}",
            instanceId, modelId));
  }

  // TODO : Stop, restart operations!
}
