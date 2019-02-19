package com.example.demo.Storage;

public class UploadFileResponse {

    private final String fileName;
    private final String workflowName;
    private final String fileDownloadUri;
    private final String fileType;
    private final long size;

    public UploadFileResponse(String fileName, String workflowName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.workflowName = workflowName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public String WorkflowName() {
        return workflowName;
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
