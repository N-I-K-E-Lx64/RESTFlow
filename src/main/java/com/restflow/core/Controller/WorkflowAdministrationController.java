package com.restflow.core.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.WorkflowDatabase.EActiveWorkflows;
import com.restflow.core.WorkflowDatabase.EWorkflowDefinitions;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.CWorkflowParseException;
import com.restflow.core.WorkflowParser.WorkflowParserService;
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

/**
 * This controller class contains functions for managing the set of workflow definitions
 */
@RestController
@RequestMapping("/workflow/administration")
public class WorkflowAdministrationController {

    private static final Logger logger = LogManager.getLogger(WorkflowAdministrationController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final StorageService mStorageService;
    private final WorkflowParserService mParserService;

    @Autowired
    public WorkflowAdministrationController(StorageService storageService, WorkflowParserService parserService) {
        mStorageService = storageService;
        mParserService = parserService;
    }

    /**
     * Function for creating a parent workflow definition
     * @param project folder containing all required files
     * @param filename filename of the file to be converted
     * @return suitable response with the name of the parent workflow definition
     */
    @RequestMapping(value = "/createWorkflowDefinition", method = RequestMethod.POST)
    public ResponseEntity<?> parseWorkflow(@RequestParam(name = "project") String project,
                                           @RequestParam(name = "workflowModel") String filename) {
        try {
            Resource lWorkflowResource = mStorageService.loadAsResource(filename, project);
            // Checks if the requested model is a workflow model at all.
            if (Objects.equals(FilenameUtils.getExtension(lWorkflowResource.getFilename()), "json")) {
                IWorkflow lWorkflowModel = mParserService.parseWorkflow(lWorkflowResource);
                EWorkflowDefinitions.INSTANCE.add(lWorkflowModel);

                return ResponseEntity.ok(MessageFormat.format(
                        "Workflow Definition [{0}] of [{1}] was successfully created! You will find it under the" +
                                " following name: {2}", project, filename, lWorkflowModel.definition()));
            } else {
                return ResponseEntity.badRequest().body(MessageFormat.format("[{0}] is not a workflow-model!", filename));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Function for deleting a parent workflow definition
     * @param workflowDefinition name of the definition to be deleted
     * @return suitable response
     */
    @RequestMapping(value = "/deleteWorkflowDefinition/{workflowDefinition:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> deleteWorkflow(@PathVariable String workflowDefinition) {

        EActiveWorkflows.INSTANCE.remove(workflowDefinition);
        EWorkflowDefinitions.INSTANCE.remove(workflowDefinition);
        mStorageService.deleteFolder(workflowDefinition);

        return ResponseEntity.ok(
                MessageFormat.format("Workflow Definition [{0}] was deleted along with all relevant files", workflowDefinition));
    }

    @ExceptionHandler(CWorkflowParseException.class)
    public ResponseEntity<String> handleWorkflowParseException(CWorkflowParseException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }
}
