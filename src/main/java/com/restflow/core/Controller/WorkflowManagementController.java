package com.restflow.core.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restflow.core.Network.Objects.CUserParameterMessage;
import com.restflow.core.Network.Responses.VariableResponse;
import com.restflow.core.WorkflowDatabase.EActiveWorkflows;
import com.restflow.core.WorkflowDatabase.EWorkflowDefinitions;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.EVariableType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This controller class contains functions for controlling the system.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/workflow")
public class WorkflowManagementController {

    private static final Logger logger = LogManager.getLogger(WorkflowManagementController.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Function for starting / creating a workflow instance
     *
     * @param definition Name of the definition on which the new workflow instance is to be based.
     * @param name       Name of the new instance
     * @return suitable response
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<?> startWorkflow(@RequestParam("definition") String definition, @RequestParam("name") String name) {

        IWorkflow lWorkflow = EWorkflowDefinitions.INSTANCE.apply(definition);

        EActiveWorkflows.INSTANCE.add(name, lWorkflow).start();

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully started [{0}]", name));
    }

    /**
     * Function for restarting a workflow instance in case of an error
     *
     * @param workflowInstance Name of the instance
     * @return suitable response
     */
    @RequestMapping(value = "/restart/{workflowInstance:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> restartWorkflow(@PathVariable String workflowInstance) {

        EActiveWorkflows.INSTANCE.restart(workflowInstance).start();

        logger.info("Restart workflow instance: " + workflowInstance);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully restarted [{0}]", workflowInstance));
    }

    /**
     * Function for stopping the execution of a specific workflow instance
     *
     * @param workflowInstance Name of the instance
     * @return suitable response
     */
    @RequestMapping(value = "/stop/{workflowInstance:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> stopWorkflow(@PathVariable String workflowInstance) {

        EActiveWorkflows.INSTANCE.remove(workflowInstance);

        logger.info(MessageFormat.format("Stopped execution of [{0}]", workflowInstance));

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully stopped [{0}]", workflowInstance));
    }

    /**
     * Function to submit a user parameter
     *
     * @param pMessage UserParameter object
     * @return suitable response
     * @see CUserParameterMessage
     */
    @RequestMapping(value = "/setUserParameter/{instanceId:.+}", method = RequestMethod.PATCH)
    public ResponseEntity<String> setUserVariable(@RequestBody CUserParameterMessage pMessage, @PathVariable String instanceId) {
        EActiveWorkflows.INSTANCE.apply(instanceId).accept(pMessage);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Parameter [{0}] was successfully overwritten with the following value [{1}]!",
                        pMessage.parameterName(), pMessage.get()));
    }

    /**
     * Monitoring function that returns the contents of all variables of a specific instance
     *
     * @param workflow Name of the instance
     * @return List containing the contents of all variables
     * @see VariableResponse
     */
    @RequestMapping(value = "/variables/{workflow:.+}", method = RequestMethod.GET)
    public List<VariableResponse> checkVariableStatus(@PathVariable String workflow) {

        Function<Map.Entry<String, IVariable>, VariableResponse> createResponse = entry -> {
            IVariable lVariable = entry.getValue();
            if (lVariable.variableType() == EVariableType.JSON) {
                try {
                    String prettyJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lVariable.value());
                    return new VariableResponse(lVariable.name(), lVariable.variableType().name(), prettyJSON);
                } catch (JsonProcessingException e) {
                    // TODO : Correct exception!
                    e.printStackTrace();
                }
            }

            return new VariableResponse(lVariable.name(), lVariable.variableType().name(), String.valueOf(lVariable.value()));
        };

        return EActiveWorkflows.INSTANCE.apply(workflow).variables().entrySet().stream()
                .map(createResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches the workflow database for suspended workflows and returns their names in a list
     *
     * @return List containing all suspended workflow names
     */
    @RequestMapping(value = "/suspendedWorkflows", method = RequestMethod.GET)
    public List<String> getSuspendedWorkflows() {
        return EActiveWorkflows.INSTANCE.get().stream()
                .filter(entry -> entry.status() == EWorkflowStatus.SUSPENDED)
                .map(IWorkflow::instance)
                .collect(Collectors.toList());
    }

    /**
     * Monitoring function that returns a description for each currently empty user variable
     *
     * @param workflow Name of the instance
     * @return List containing a CUserParameterMessage for each empty variable
     * @see CUserParameterMessage
     */
    @RequestMapping(value = "/userParams/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<List<CUserParameterMessage>> getUserParams(@PathVariable String workflow) {
        // Creates a UserParameterMessage with an empty string as value
        return ResponseEntity.ok(EActiveWorkflows.INSTANCE.apply(workflow).emptyVariables().stream()
                .map(param -> new CUserParameterMessage(workflow, param.id(), ""))
                .collect(Collectors.toList()));
    }

    /**
     * This monitoring allows to describe the status of a specific workflow instance in detail
     *
     * @param workflow Name of the instance
     * @return A report that describes the current state of the instance
     */
    @RequestMapping(value = "/status/{workflow:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkStatus(@PathVariable String workflow) {

        final IWorkflow lWorkflow = EActiveWorkflows.INSTANCE.apply(workflow);

        switch (lWorkflow.status()) {
            case ACTIVE:
                ObjectNode lWorkingNode = mapper.createObjectNode();
                lWorkingNode.put("status", lWorkflow.status().get());
                lWorkingNode.put("currentTask", lWorkflow.currentTask().title());

                return ResponseEntity.ok(lWorkingNode);

            case COMPLETE:
                ArrayNode lVariables = mapper.valueToTree(checkVariableStatus(workflow));
                ObjectNode lFinishedNode = mapper.createObjectNode();
                lFinishedNode.put("status", lWorkflow.status().get());
                lFinishedNode.put("message", MessageFormat.format("Workflow [{0}] is completed", workflow));
                lFinishedNode.putArray("variables").addAll(lVariables);

                return ResponseEntity.ok(lFinishedNode);

            case SUSPENDED:
                ArrayNode lEmptyVariables = mapper.valueToTree(lWorkflow.emptyVariables());
                ObjectNode lWaitingNode = mapper.createObjectNode();
                lWaitingNode.put("status", lWorkflow.status().get());
                lWaitingNode.put("currentTask", lWorkflow.currentTask().title());
                lWaitingNode.putArray("emptyVariables").addAll(lEmptyVariables);

                return ResponseEntity.ok(lWaitingNode);

            case TERMINATED:
                ObjectNode lErrorNode = mapper.createObjectNode();
                lErrorNode.put("status", lWorkflow.status().get());
                //TODO: Fehlerbeschreibung!
                return ResponseEntity.status(500).body(lErrorNode);

            default:
                return ResponseEntity.notFound().build();
        }
    }

    // TODO : Delete this

    /**
     * Monitoring function that returns a short description of all active workflow instances
     *
     * @return Short description of all active workflow instances
     */
    /*@RequestMapping(value = "/workflows", method = RequestMethod.GET)
    public List<WorkflowListResponse> sendWorkflowList() {

        Function<Map.Entry<String, IWorkflow>, WorkflowListResponse> createResponse = entry -> {
            String lModelName = entry.getValue().definition() + "-MODEL";
            String lCurrentStatus = entry.getValue().status().get();
            String lCurrentTask = entry.getValue().currentTask().title();

            return new WorkflowListResponse(entry.getKey(), lModelName, lCurrentStatus, lCurrentTask);
        };

        return EActiveWorkflows.INSTANCE.get().stream()
                .map(createResponse)
                .collect(Collectors.toList());
    }*/

    // TODO : Generic!
    @ExceptionHandler(CUserInteractionException.class)
    public ResponseEntity<?> handleUserInteractionException(CUserInteractionException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}
