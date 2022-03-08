package com.restflow.core.Network.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadFileResponse {

    @JsonProperty("fileName")
    private final String fileName;
    @JsonProperty("project")
    private final String project;
    @JsonProperty("downloadUrl")
    private final String fileDownloadUri;
    @JsonProperty("mediaType")
    private final String fileType;
    @JsonProperty("fileSize")
    private final long size;

    public UploadFileResponse(String fileName, String project, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.project = project;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public String getProject() {
        return project;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public long getSize() {
        return size;
    }
}
