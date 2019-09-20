package com.restflow.core.Storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Objects;

@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger logger = LogManager.getLogger(FileSystemStorageService.class);

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.location());
    }

    @Override
    public void init() {

        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new StorageExecption("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    public void deleteAll() {

        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void deleteFolder(String workflowName) {

        Path workflowFolder = rootLocation.resolve(workflowName);
        FileSystemUtils.deleteRecursively(workflowFolder.toFile());
    }

    @Override
    public void initWorkflowDirectory(String workflowName) {
        try {
            if (!Files.exists(this.rootLocation.resolve(workflowName))) {
                Path directoryPath = this.rootLocation.resolve(workflowName);
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new StorageExecption("Could not create the workflow directory where the uploaded files will be stored", e);
        }
    }

    public String store(@NonNull final MultipartFile file, @NonNull final String workflow) {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (file.isEmpty()) {
                throw new StorageExecption("Failed to store empty file" + filename);
            }
            // SecurityCheck
            if (filename.contains("..")) {
                throw new StorageExecption("Cannot store file with relative path outside current directory " + filename);
            }

            Path workflowLocation = this.rootLocation.resolve(workflow);
            Path targetLocation = workflowLocation.resolve(filename);
            logger.info("Save file on Location: " + targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;

        } catch (IOException e) {
            throw new StorageExecption("Failed to store file " + filename, e);
        }
    }

    public Resource loadAsResource(@NonNull final String filename, @NonNull final String workflowName) {
        try {
            Path workflowLocation = this.rootLocation.resolve(workflowName);
            Path fileLocation = workflowLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(fileLocation.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                logger.error(MessageFormat.format(
                        "File {0} on location {3} exists: {1}; isReadable: {2}",
                        filename, resource.exists(), resource.isReadable(), fileLocation.toUri()));

                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
}
