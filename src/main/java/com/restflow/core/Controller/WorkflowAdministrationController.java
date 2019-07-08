package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restflow.core.ERunningWorkflows;
import com.restflow.core.EWorkflowDefinitons;
import com.restflow.core.Storage.StorageService;
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
import java.util.Objects;

@RestController
@RequestMapping("/workflow/administration")
public class WorkflowAdministrationController {

    private static final Logger logger = LogManager.getLogger(WorkflowAdministrationController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final StorageService mStorageService;

    @Autowired
    public WorkflowAdministrationController(StorageService storageService) {
        mStorageService = storageService;
        EWorkflowParser.INSTANCE.init(storageService);
    }

    @RequestMapping(value = "/parseWorkflow", method = RequestMethod.POST)
    public ResponseEntity<?> parseWorkflow(@RequestParam(name = "workflow") String workflow, @RequestParam(name = "filename") String filename) {

        Resource lWorkflowResource = mStorageService.loadAsResource(filename, workflow);
        if (Objects.equals(FilenameUtils.getExtension(lWorkflowResource.getFilename()), "json")) {
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
        lSuccessNode.put("file", filename);

        return ResponseEntity.ok(lSuccessNode);
    }

    @RequestMapping(value = "/deleteWorkflow/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> deleteWorkflow(@PathVariable String workflow) {

        ERunningWorkflows.INSTANCE.remove(workflow);
        EWorkflowDefinitons.INSTANCE.remove(workflow);
        mStorageService.deleteFolder(workflow);

        return ResponseEntity.ok(
                MessageFormat.format("Workflow [{0}] was deleted along with all relevant files", workflow));
    }

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity handleWorkflowParseException(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}
