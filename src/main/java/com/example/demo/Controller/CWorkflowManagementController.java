package com.example.demo.Controller;

import com.example.demo.EWorkflowStorage;
import com.example.demo.Network.IMessage;
import com.example.demo.Storage.StorageService;
import com.example.demo.WorkflowExecution.Objects.CUserInteractionException;
import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.CParameterFactory;
import com.example.demo.WorkflowParser.CWorkflowParseException;
import com.example.demo.WorkflowParser.EWorkflowParser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.MessageFormat;

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

    @ResponseBody
    @RequestMapping(value = "/parseWorkflow", method = RequestMethod.POST)
    public ResponseEntity parseWorkflow(@RequestParam("workflow") String workflow, @RequestParam("workflowFile") String workflowFile) {

        Resource lWorkflowResource = mStorageService.loadAsResource(workflowFile, workflow);
        if (FilenameUtils.getExtension(lWorkflowResource.getFilename()).equals("json")) {
            try {
                EWorkflowStorage.INSTANCE.add(EWorkflowParser.INSTANCE.parseWorkflow(lWorkflowResource));
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
    public ResponseEntity<String> startWorkflow(@PathVariable String workflow) {

        EWorkflowStorage.INSTANCE.apply(workflow).start();

        return ResponseEntity.ok(MessageFormat.format("Successfully started [{0}]", workflow));
    }

    @RequestMapping(value = "/setUserParameter", method = RequestMethod.POST)
    public ResponseEntity setUserVariable(@RequestBody CMessage pMessage) {

        final IWorkflow lWorkflow = EWorkflowStorage.INSTANCE.apply(pMessage.workflow());

        lWorkflow.accept(pMessage);

        ObjectNode lSuccessNode = mapper.createObjectNode();
        lSuccessNode.put("message", MessageFormat.format(
                "Parameter [{0}] was successfully overwritten with the following value [{1}]!",
                pMessage.parameterName(), pMessage.parameterValue()));

        return ResponseEntity.ok(lSuccessNode);
    }

    @RequestMapping(value = "/status/{workflow:.+}")
    public ResponseEntity<?> checkStatus(@PathVariable String workflow) {

        final IWorkflow lWorkflow = EWorkflowStorage.INSTANCE.apply(workflow);

        switch (lWorkflow.status()) {
            case WORKING:
                ObjectNode lWorkingNode = mapper.createObjectNode();
                lWorkingNode.put("type", lWorkflow.status().get());
                lWorkingNode.put("currentTask", lWorkflow.currentTask().title());

                return ResponseEntity.ok(lWorkingNode);

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

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity handleWorkflowParseExceptipn(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(CUserInteractionException.class)
    public ResponseEntity handleUserInteractionException(CUserInteractionException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
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
