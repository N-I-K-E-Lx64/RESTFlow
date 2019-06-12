package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @RequestMapping(value = "/parseWorkflow")
    public ResponseEntity<?> parseWorkflow(@RequestParam(name = "workflow") String workflow, @RequestParam(name = "filename") String filename) {

        Resource lWorkflowResource = mStorageService.loadAsResource(filename, workflow);
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
        lSuccessNode.put("file", filename);

        return ResponseEntity.ok(lSuccessNode);
    }

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity handleWorkflowParseException(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}
