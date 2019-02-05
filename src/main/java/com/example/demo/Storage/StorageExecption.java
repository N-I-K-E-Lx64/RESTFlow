package com.example.demo.Storage;

public class StorageExecption extends RuntimeException {

    public StorageExecption(String message) {
        super(message);
    }

    public StorageExecption(String message, Throwable cause) {
        super(message, cause);
    }
}
