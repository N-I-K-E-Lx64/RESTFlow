package com.restflow.core.Controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restflow.core.ERunningWorkflows;
import com.restflow.core.EWorkflowDefinitons;
import com.restflow.core.Network.IMessage;
import com.restflow.core.Responses.CVariableResponse;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.CParameterFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow")
public class WorkflowManagementController {

    private static final Logger logger = LogManager.getLogger(WorkflowManagementController.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/start/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> startWorkflow(@PathVariable String workflow) {

        IWorkflow lWorkflow = EWorkflowDefinitons.INSTANCE.apply(workflow);

        ERunningWorkflows.INSTANCE.add(lWorkflow);

        logger.info("Start Workflow: " + workflow);

        lWorkflow.start();

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully started [{0}]", workflow));
    }

    @RequestMapping(value = "/restart/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> restartWorkflow(@PathVariable String workflow) {
        IWorkflow lWorkflow1 = ERunningWorkflows.INSTANCE.apply(workflow);
        ERunningWorkflows.INSTANCE.remove(workflow);

        IWorkflow lWorkflow = EWorkflowDefinitons.INSTANCE.apply(workflow);

        if (lWorkflow1.equals(lWorkflow)) {
            logger.error("No deep Copy!");
        }

        logger.info("Restart Workflow: " + workflow);

        ERunningWorkflows.INSTANCE.add(lWorkflow).start();

        return ResponseEntity.ok(MessageFormat.format("Successfully restarted [{0}]", workflow));
    }

    @RequestMapping(value = "/stop/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> stopWorkflow(@PathVariable String workflow) {
        ERunningWorkflows.INSTANCE.remove(workflow);

        logger.info("Stopped Workflow: " + workflow);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully stopped [{0}]", workflow));
    }


    @RequestMapping(value = "/status/{workflow:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkStatus(@PathVariable String workflow) {

        final IWorkflow lWorkflow = ERunningWorkflows.INSTANCE.apply(workflow);

        switch (lWorkflow.status()) {
            case WORKING:
                ObjectNode lWorkingNode = mapper.createObjectNode();
                lWorkingNode.put("type", lWorkflow.status().get());
                lWorkingNode.put("currentTask", lWorkflow.currentTask().title());

                return ResponseEntity.ok(lWorkingNode);

            case FINISHED:
                ArrayNode lVariables = mapper.valueToTree(checkVariableStatus(workflow));
                ObjectNode lFinishedNode = mapper.createObjectNode();
                lFinishedNode.put("message", MessageFormat.format("Workflow [{0}] is completed", workflow));
                lFinishedNode.putArray("variables").addAll(lVariables);

                return ResponseEntity.ok(lFinishedNode);

            case WAITING:
                ArrayNode lEmptyVariables = mapper.valueToTree(lWorkflow.emptyVariables());
                ObjectNode lWaitingNode = mapper.createObjectNode();
                lWaitingNode.put("type", lWorkflow.status().get());
                lWaitingNode.put("title", lWorkflow.currentTask().title());
                lWaitingNode.putArray("emptyVariables").addAll(lEmptyVariables);

                return ResponseEntity.ok(lWaitingNode);

            case ERROR:
                ObjectNode lErrorNode = mapper.createObjectNode();
                lErrorNode.put("type", lWorkflow.status().get());

                return ResponseEntity.status(500).body(lErrorNode);

            default:
                return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/variables/{workflow:.+}", method = RequestMethod.GET)
    public List<CVariableResponse> checkVariableStatus(@PathVariable String workflow) {

        final IWorkflow lWorkflow = ERunningWorkflows.INSTANCE.apply(workflow);

        return lWorkflow.variables().entrySet().stream()
                .map(variable -> createVariableResponse(variable.getKey(), variable.getValue().value()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/setUserParameter", method = RequestMethod.POST)
    public ResponseEntity setUserVariable(@RequestBody CMessage pMessage) {

        final IWorkflow lWorkflow = ERunningWorkflows.INSTANCE.apply(pMessage.workflow());

        lWorkflow.accept(pMessage);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Parameter [{0}] was successfully overwritten with the following value [{1}]!",
                        pMessage.parameterName(), pMessage.parameterValue()));
    }

    private CVariableResponse createVariableResponse(String pVariableName, Object pVariableValue) {
        if (pVariableValue instanceof String) {
            ObjectNode lStringNode = mapper.createObjectNode();
            lStringNode.put("text", (String) pVariableValue);

            return new CVariableResponse(pVariableName, lStringNode);
        }
        return new CVariableResponse(pVariableName, (JsonNode) pVariableValue);
    }

    @ExceptionHandler(CUserInteractionException.class)
    public ResponseEntity handleUserInteractionException(CUserInteractionException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }

    /**
     * Class for representing the messages!
     */
    public static final class CMessage implements IMessage {

        @JsonProperty("workflow")
        private String mWorkflow;
        @JsonProperty("parameter")
        private String mParameter;
        @JsonProperty("type")
        private String mParameterType;
        @JsonProperty("value")
        private String mParameterValue;


        //TODO : Implement the get Method!
        @Override
        public String get() {
            return null;
        }

        public String workflow() {
            return mWorkflow;
        }

        public String parameterName() {
            return mParameter;
        }

        public Object parameterValue() {
            return CParameterFactory.getInstance().createParameterValue(mParameterType, mParameterValue);
        }
    }
}
