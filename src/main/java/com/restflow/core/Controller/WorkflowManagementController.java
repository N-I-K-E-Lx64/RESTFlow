package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restflow.core.EActiveWorkflows;
import com.restflow.core.EWorkflowDefinitions;
import com.restflow.core.Network.Objects.CUserParameterMessage;
import com.restflow.core.Responses.VariableResponse;
import com.restflow.core.Responses.WorkflowListResponse;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
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

@RestController
@RequestMapping("/workflow")
public class WorkflowManagementController {

    private static final Logger logger = LogManager.getLogger(WorkflowManagementController.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseEntity<?> startWorkflow(@RequestParam("definition") String workflowDefinition, @RequestParam("name") String workflowName) {

        IWorkflow lWorkflow = EWorkflowDefinitions.INSTANCE.apply(workflowDefinition);

        EActiveWorkflows.INSTANCE.add(workflowName, lWorkflow).start();

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully started [{0}]", workflowName));
    }

    @RequestMapping(value = "/restart/{workflowInstance:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> restartWorkflow(@PathVariable String workflowInstance) {

        EActiveWorkflows.INSTANCE.restart(workflowInstance).start();

        logger.info("Restart workflow instance: " + workflowInstance);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully restarted [{0}]", workflowInstance));
    }

    @RequestMapping(value = "/stop/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> stopWorkflow(@PathVariable String workflow) {

        EActiveWorkflows.INSTANCE.remove(workflow);

        logger.info(MessageFormat.format("Stopped execution of [{0}]", workflow));

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully stopped [{0}]", workflow));
    }

    @RequestMapping(value = "/setUserParameter", method = RequestMethod.POST)
    public ResponseEntity setUserVariable(@RequestBody CUserParameterMessage pMessage) {

        EActiveWorkflows.INSTANCE.apply(pMessage.getWorkflowInstance()).accept(pMessage);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Parameter [{0}] was successfully overwritten with the following value [{1}]!",
                        pMessage.parameterName(), pMessage.get()));
    }

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

    @RequestMapping(value = "/variables/{workflow:.+}", method = RequestMethod.GET)
    public List<VariableResponse> checkVariableStatus(@PathVariable String workflow) {

        Function<Map.Entry<String, IVariable>, VariableResponse> createResponse = entry -> {
            IVariable lVariable = entry.getValue();
            if (lVariable.value() instanceof String) {
                ObjectNode lStringNode = mapper.createObjectNode();
                lStringNode.put("String Value", (String) lVariable.value());

                return new VariableResponse(lVariable.name(), lStringNode);
            }
            return new VariableResponse(lVariable.name(), (JsonNode) lVariable.value());
        };

        return EActiveWorkflows.INSTANCE.apply(workflow).variables().entrySet().stream()
                            .map(createResponse)
                            .collect(Collectors.toList());

    }

    @RequestMapping(value = "/workflows", method = RequestMethod.GET)
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
    }

    @ExceptionHandler(CUserInteractionException.class)
    public ResponseEntity handleUserInteractionException(CUserInteractionException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}