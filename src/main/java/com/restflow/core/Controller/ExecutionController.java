package com.restflow.core.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.ModelingTool.ModelService;
import com.restflow.core.Network.Objects.CUserParameterMessage;
import com.restflow.core.Network.Responses.VariableResponse;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowExecution.WorkflowService;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ExecutionController {

  private static final Logger logger = LogManager.getLogger(ExecutionController.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  private final ModelService modelService;
  private final WorkflowService workflowService;

  @Autowired
  public ExecutionController(ModelService modelService, WorkflowService workflowService) {
    this.modelService = modelService;
    this.workflowService = workflowService;
  }

  /**
   * Endpoint for starting / creating a workflow instance
   *
   * @param modelId    Id of the model on which the new instance is based on.
   * @param instanceId Id of the instance
   * @return suitable response
   */
  @RequestMapping(value = "/execute/{modelId:.+}/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<String> startModelExecution(@PathVariable UUID modelId,
      @PathVariable String instanceId) {
    this.workflowService.accept(this.modelService.apply(modelId), instanceId);

    return ResponseEntity.ok(
        MessageFormat.format("Successfully created a new instance {0} from the model {1}",
            instanceId, modelId));
  }

  /**
   * Endpoint for restarting a specific workflow instance
   *
   * @param instanceId ID of the instance to be restarted
   * @return suitable response
   */
  @RequestMapping(value = "/restart/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<String> restartWorkflow(@PathVariable String instanceId) {
    this.workflowService.restart(instanceId);

    return ResponseEntity.ok(
        MessageFormat.format("Restarted the execution of instance {0}", instanceId));
  }

  /**
   * Endpoint for stopping the execution of a specific workflow instance
   *
   * @param instanceId ID of the instance to be restarted
   * @return suitable response
   */
  @RequestMapping(value = "/stop/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<String> stopInstance(@PathVariable String instanceId) {
    this.workflowService.apply(instanceId).stop();

    return ResponseEntity.ok(
        MessageFormat.format("Execution of instance {0} is stopped.", instanceId));
  }

  /*@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteInstances(@RequestBody List<String> instances) {
    instances.forEach(this.workflowService::remove);

    return ResponseEntity.ok();
  }*/

  /**
   * Searches the "workflow database" for suspended workflows and returns their names in a list
   *
   * @return List containing all suspended workflow names
   */
  @RequestMapping(value = "/suspendedWorkflows", method = RequestMethod.GET)
  public ResponseEntity<List<String>> getSuspendedWorkflows() {
    final List<String> suspendedWorkflows = this.workflowService.get().stream()
        .filter(iWorkflow -> iWorkflow.status() == EWorkflowStatus.SUSPENDED)
        .map(IWorkflow::instance)
        .toList();

    return ResponseEntity.ok(suspendedWorkflows);
  }

  /**
   * Monitoring function that returns a description for each currently empty user variable
   *
   * @param instanceId Name of the instance
   * @return List containing a CUserParameterMessage for each empty variable
   * @see CUserParameterMessage
   */
  @RequestMapping(value = "/userParams/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<List<CUserParameterMessage>> getUserParams(
      @PathVariable String instanceId) {
    // Creates a list of UserParameterMessage with an empty string as value
    final List<CUserParameterMessage> response = this.workflowService.apply(instanceId)
        .emptyVariables().stream()
        .map(iParameter -> new CUserParameterMessage(instanceId, iParameter.id(), ""))
        .toList();

    logger.info("User-parameter request for workflow instance " + instanceId);

    return ResponseEntity.ok(response);
  }

  /**
   * Monitoring function that returns the contents of all variables of a specific instance
   *
   * @param instanceId Name of the instance
   * @return List containing the contents of all variables
   * @see VariableResponse
   */
  @RequestMapping(value = "/variables/{instanceId:.+}", method = RequestMethod.GET)
  public ResponseEntity<?> getVariables(@PathVariable String instanceId) {
    // Function to create a VariableResponse
    Function<Entry<String, IVariable<?>>, VariableResponse> createResponse = entry -> {
      IVariable<?> variable = entry.getValue();
      if (variable.type() == JsonNode.class) {
        try {
          String prettyJSON = mapper.writerWithDefaultPrettyPrinter()
              .writeValueAsString(variable.value());
          return new VariableResponse(variable.id(), variable.type().getSimpleName(), prettyJSON);
        } catch (JsonProcessingException ex) {
          logger.error(
              MessageFormat.format("Json variable {0} could not be parsed!", variable.id()));
        }
      }
      return new VariableResponse(variable.id(), variable.type().getName(),
          String.valueOf(variable.value()));
    };

    final List<VariableResponse> response = this.workflowService.apply(instanceId).variables()
        .entrySet().stream()
        .map(createResponse)
        .toList();

    return ResponseEntity.ok(response);
  }

  /**
   * Function to submit a user parameter
   *
   * @param message UserParameter object
   * @return suitable response
   * @see CUserParameterMessage
   */
  @RequestMapping(value = "/setUserParameter/{instanceId:.+}", method = RequestMethod.PATCH)
  public ResponseEntity<String> submitUserParameter(@RequestBody CUserParameterMessage message,
      @PathVariable String instanceId) {
    // Transfer the message to the correct task
    this.workflowService.apply(instanceId).accept(message);

    return ResponseEntity.ok(MessageFormat.format(
        "Parameter [{0}] was successfully overwritten with the following value [{1}]!",
        message.parameterName(), message.get()));
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<?> handleException(RuntimeException ex) {
    logger.error(ex);
    logger.error(ex.getMessage());
    return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN)
        .body(ex.getMessage());
  }
}