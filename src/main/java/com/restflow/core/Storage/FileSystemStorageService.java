package com.restflow.core.Storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
      throw new StorageExecption(
          "Could not create the directory where the uploaded files will be stored", e);
    }
  }

  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  public void deleteFolder(String workflowName) {
    Path workflowFolder = rootLocation.resolve(workflowName);
    FileSystemUtils.deleteRecursively(workflowFolder.toFile());
  }

  public void deleteFile(@NonNull String fileName) {
    Path delete = rootLocation.resolve(fileName);
    FileSystemUtils.deleteRecursively(delete.toFile());

    logger.info("Deleted file: " + fileName);
  }

  @Override
  public void initWorkflowDirectory(String workflowName) {
    try {
      if (!Files.exists(this.rootLocation.resolve(workflowName))) {
        Path directoryPath = this.rootLocation.resolve(workflowName);
        Files.createDirectories(directoryPath);
      }
    } catch (IOException e) {
      throw new StorageExecption(
          "Could not create the workflow directory where the uploaded files will be stored", e);
    }
  }

  /**
   * Creates a new file and writes the pretty formatted JSON string to it
   *
   * @param json     JSON String
   * @param filename Filename
   */
  public void storeModel(@NonNull final String json, @NonNull final String filename) {
    try {
      // Security Checks
      if (json.isEmpty()) {
        throw new StorageExecption("Cannot store empty file" + filename);
      }

      if (filename.contains("..")) {
        throw new StorageExecption(
            "Cannot store file with relative path outside current directory " + filename);
      }

      Path targetLocation = this.rootLocation.resolve(filename + ".json");
      if (!Files.exists(targetLocation)) {
        Files.createFile(targetLocation);
      }
      Files.writeString(targetLocation, json, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      logger.info("Saved model on Location: " + targetLocation);
    } catch (IOException e) {
      throw new StorageExecption(
          MessageFormat.format("Failed to write JSON string to file [{0}]!", filename));
    }
  }

  public List<File> loadAllFilesFromFolder(@NonNull final String folder) {
    Path folderLocation = this.rootLocation.resolve(folder).normalize();
    try {
      try (Stream<Path> stream = Files.list(folderLocation)) {
        return stream.filter(file -> !Files.isDirectory(file))
            .map(Path::toFile)
            .toList();
      }
    } catch (IOException ex) {
      throw new StorageExecption("Can not traverse : " + folder, ex);
    }
  }

  public List<File> loadAllModels() {
    try {
      try (Stream<Path> stream = Files.list(this.rootLocation)) {
        return stream.filter(file -> !Files.isDirectory(file))
            .filter(file -> FilenameUtils.getExtension(file.toString()).equals("json"))
            .map(Path::toFile)
            .toList();
      }
    } catch (IOException ex) {
      throw new StorageExecption("Can not traverse root folder!", ex);
    }
  }

  public StorageConfirmation store(@NonNull final MultipartFile file,
      @NonNull final String workflow) {

    String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    try {
      if (file.isEmpty()) {
        throw new StorageExecption("Failed to store empty file" + filename);
      }
      // SecurityCheck
      if (filename.contains("..")) {
        throw new StorageExecption(
            "Cannot store file with relative path outside current directory " + filename);
      }

      Path workflowLocation = this.rootLocation.resolve(workflow);
      Path targetLocation = workflowLocation.resolve(filename);
      logger.info("Save file on Location: " + targetLocation);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return new StorageConfirmation(filename, targetLocation.toFile());
    } catch (IOException e) {
      throw new StorageExecption("Failed to store file " + filename, e);
    }
  }
}
