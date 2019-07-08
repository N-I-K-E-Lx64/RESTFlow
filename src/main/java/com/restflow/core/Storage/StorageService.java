package com.restflow.core.Storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void deleteAll();

    void deleteFolder(String workflowName);

    void initWorkflowDirectory(String workflowName);

    String store(MultipartFile file, String workflowName);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename, String workflowName);
}
