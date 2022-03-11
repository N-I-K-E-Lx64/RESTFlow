package com.restflow.core.Storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    void deleteAll();

    void deleteFolder(String workflowName);

    void initWorkflowDirectory(String workflowName);

    void storeModel(String json, String filename);

    Resource loadModelAsResource(String filename);

    String store(MultipartFile file, String workflowName);

    Resource loadAsResource(String filename, String workflowName);
}
