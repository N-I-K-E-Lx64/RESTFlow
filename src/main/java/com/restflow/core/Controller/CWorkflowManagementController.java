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
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.CParameterFactory;
import com.restflow.core.WorkflowParser.CWorkflowParseException;
import com.restflow.core.WorkflowParser.EWorkflowParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow")
public class CWorkflowManagementController {

    private static final Logger logger = LogManager.getLogger(CWorkflowManagementController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final StorageService mStorageService;

    @Autowired
    public CWorkflowManagementController(StorageService storageService) {
        mStorageService = storageService;
        EWorkflowParser.INSTANCE.init(storageService);
    }

    @RequestMapping(value = "/parseWorkflow", method = RequestMethod.POST)
    public ResponseEntity<?> parseWorkflow(@RequestParam("workflow") String workflow, @RequestParam("workflowFile") String workflowFile) {

        Resource lWorkflowResource = mStorageService.loadAsResource(workflowFile, workflow);
        if (FilenameUtils.getExtension(lWorkflowResource.getFilename()).equals("json")) {
            try {
                EWorkflowDefinitons.INSTANCE.add(EWorkflowParser.INSTANCE.parseWorkflow(lWorkflowResource));
            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (CWorkflowParseException e) {
                logger.error(e);
            }
        }

        ObjectNode lSuccessNode = mapper.createObjectNode();
        lSuccessNode.put("message", "Workflow was successfully parsed!");
        lSuccessNode.put("workflow", workflow);
        lSuccessNode.put("file", workflowFile);

        return ResponseEntity.ok(lSuccessNode);
    }

    @RequestMapping(value = "/start/{workflow:.+}")
    public ResponseEntity<?> startWorkflow(@PathVariable String workflow) {

        IWorkflow lWorkflow = EWorkflowDefinitons.INSTANCE.apply(workflow);

        ERunningWorkflows.INSTANCE.add(lWorkflow);

        lWorkflow.start();

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully started [{0}]", workflow));
    }

    @RequestMapping(value = "/restart/{workflow:.+}")
    public ResponseEntity<?> restartWorkflow(@PathVariable String workflow) {

        IWorkflow lWorkflow1 = ERunningWorkflows.INSTANCE.apply(workflow);
        ERunningWorkflows.INSTANCE.remove(workflow);

        IWorkflow lWorkflow = EWorkflowDefinitons.INSTANCE.apply(workflow);

        if (lWorkflow1.equals(lWorkflow)) {
            logger.error("No deep Copy!");
        }

        ERunningWorkflows.INSTANCE.add(lWorkflow).start();

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Successfully restarted [{0}]", workflow));

    }

    @RequestMapping(value = "/setUserParameter", method = RequestMethod.POST)
    public ResponseEntity setUserVariable(@RequestBody CMessage pMessage) {

        final IWorkflow lWorkflow = ERunningWorkflows.INSTANCE.apply(pMessage.workflow());

        lWorkflow.accept(pMessage);

        return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN)
                .body(MessageFormat.format("Parameter [{0}] was successfully overwritten with the following value [{1}]!",
                pMessage.parameterName(), pMessage.parameterValue()));
    }

    @RequestMapping(value = "/status/{workflow:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/variables/{workflow:.+}")
    public List<CVariableResponse> checkVariableStatus(@PathVariable String workflow) {

        final IWorkflow lWorkflow = ERunningWorkflows.INSTANCE.apply(workflow);

        //TODO better solution
        return lWorkflow.variables().entrySet().stream()
                .map(variable -> createVariableResponse(variable.getKey(), variable.getValue().value()))
                .collect(Collectors.toList());
    }

    private CVariableResponse createVariableResponse(String pVariableName, Object pVariableValue) {
        return new CVariableResponse(pVariableName, (JsonNode) pVariableValue);
    }

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity handleWorkflowParseExceptipn(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
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
