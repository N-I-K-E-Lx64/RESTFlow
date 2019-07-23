package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.EActiveWorkflows;
import com.restflow.core.EWorkflowDefinitions;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
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

    @RequestMapping(value = "/createWorkflowDefinition", method = RequestMethod.POST)
    public ResponseEntity<?> parseWorkflow(@RequestParam(name = "project") String project, @RequestParam(name = "workflowModel") String filename) {

        Resource lWorkflowResource = mStorageService.loadAsResource(filename, project);
        IWorkflow lWorkflowModel = null;

        if (Objects.equals(FilenameUtils.getExtension(lWorkflowResource.getFilename()), "json")) {
            try {
                lWorkflowModel = EWorkflowParser.INSTANCE.parseWorkflow(lWorkflowResource);
                EWorkflowDefinitions.INSTANCE.add(lWorkflowModel);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        if (Objects.isNull(lWorkflowModel)) {
            throw new CWorkflowParseException(MessageFormat.format(
                    "Workflow Definition [{0}] of [{1}] could not be created!", project, filename));
        }

        return ResponseEntity.ok(MessageFormat.format(
                "Workflow Definition [{0}] of [{1}] was successfully created! You will find it under the" +
                        " following name: {2}", project, filename, lWorkflowModel.definition()));
    }

    @RequestMapping(value = "/deleteWorkflowDefinition/{workflow:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> deleteWorkflow(@PathVariable String workflow) {

        EActiveWorkflows.INSTANCE.remove(workflow);
        EWorkflowDefinitions.INSTANCE.remove(workflow);
        mStorageService.deleteFolder(workflow);

        return ResponseEntity.ok(
                MessageFormat.format("Workflow Definition [{0}] was deleted along with all relevant files", workflow));
    }

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity handleWorkflowParseException(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}
