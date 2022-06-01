package com.restflow.core.Storage;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface StorageService {

    void init();

    void deleteAll();

    void deleteFolder(String workflowName);

    void deleteFile(@NonNull String fileName);

    void initWorkflowDirectory(String workflowName);

    void storeModel(String json, String filename);

    List<File> loadAllFilesFromFolder(String folder);

    List<File> loadAllModels();

    StorageConfirmation store(MultipartFile file, String workflowName);
}
