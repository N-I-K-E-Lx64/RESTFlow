package com.example.demo.Controller;

import com.example.demo.Storage.StorageFileNotFoundException;
import com.example.demo.Storage.StorageService;
import com.example.demo.Storage.UploadFileResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileUploadController {

    private static final Logger logger = LogManager.getLogger(FileUploadController.class);

    private final StorageService mStorageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        mStorageService = storageService;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("workflow") String workflowName) {

        mStorageService.initWorkflowDirectory(workflowName);

        String fileName = mStorageService.store(file, workflowName);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(file.getOriginalFilename(), workflowName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                        @RequestParam("workflow") String workflowName) {

        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file, workflowName))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{workflowName:.+}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String workflowName, @PathVariable String fileName,
                                                 HttpServletRequest request) {

        // Load file as Resource
        Resource resource = mStorageService.loadAsResource(fileName, workflowName);

        // Determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined.
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
