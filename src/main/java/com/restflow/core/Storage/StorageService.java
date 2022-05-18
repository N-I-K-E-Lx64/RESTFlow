package com.restflow.core.Storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

    Resource loadModelAsResource(String filename);

    List<File> loadAllModels();

    String store(MultipartFile file, String workflowName);

    Resource loadAsResource(String filename, String workflowName);
}
