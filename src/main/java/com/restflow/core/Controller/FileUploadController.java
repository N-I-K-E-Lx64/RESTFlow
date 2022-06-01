package com.restflow.core.Controller;

import com.restflow.core.Network.Responses.UploadRamlResponse;
import com.restflow.core.Storage.StorageFileNotFoundException;
import com.restflow.core.Storage.StorageService;
import com.restflow.core.ThrowingFunction;
import com.restflow.core.WorkflowParser.RamlParserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class FileUploadController {

    private static final Logger logger = LogManager.getLogger(FileUploadController.class);

    private final StorageService storageService;

    private final RamlParserService ramlParserService;

    @Autowired
    public FileUploadController(StorageService storageService, RamlParserService ramlParserService) {
        this.storageService = storageService;
        this.ramlParserService = ramlParserService;
    }

    /**
     * Checks whether the uploaded files are raml files and saves them in the correct folder
     * @param files RAML-files to be saved
     * @param modelId Id of the corresponding model (name of the folder)
     * @return List of all resources from this raml-file
     */
    @RequestMapping(value = "/uploadFile/{modelId:.+}", method = RequestMethod.PUT)
    public ResponseEntity<List<UploadRamlResponse>> uploadRamlFile(@RequestParam("files") MultipartFile[] files, @PathVariable String modelId) {
        this.storageService.initWorkflowDirectory(modelId);

        List<UploadRamlResponse> response = Arrays.stream(files)
                .filter(multipartFile -> Objects.equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()), "raml"))
                .map(file -> this.storageService.store(file, modelId))
                .map(storageConfirmation -> this.ramlParserService.parseRaml(storageConfirmation.file(), modelId))
                .filter(Objects::nonNull)
                .map(apiStorage -> new UploadRamlResponse(apiStorage.fileName(), apiStorage.resources()))
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Loads the parsed API description from the state and returns the relative url of every resource
     * @param modelId Id of the corresponding model (name of the folder)
     * @return List of all Resources from this raml-file
     */
    @RequestMapping(value = "/ramlFiles/{modelId:.+}", method = RequestMethod.GET)
    public ResponseEntity<List<UploadRamlResponse>> getApiResources(@PathVariable String modelId) {
        try {
            List<UploadRamlResponse> response = this.ramlParserService.getRamlFilesForModel(modelId)
                    .stream()
                    .map(apiStorage -> new UploadRamlResponse(apiStorage.fileName(), apiStorage.resources()))
                    .toList();

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.info(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
