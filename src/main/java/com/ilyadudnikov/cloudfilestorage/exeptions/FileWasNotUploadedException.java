package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FileWasNotUploadedException extends RuntimeException {
    public FileWasNotUploadedException(String message) {
        super(message);
    }
}
