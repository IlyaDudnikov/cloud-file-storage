package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FolderNotUploadedException extends RuntimeException {
    public FolderNotUploadedException(String message) {
        super(message);
    }
}
