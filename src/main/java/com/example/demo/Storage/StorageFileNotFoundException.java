package com.example.demo.Storage;

public class StorageFileNotFoundException extends StorageExecption {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
