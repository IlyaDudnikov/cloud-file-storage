package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) {
        super(message);
    }
}
