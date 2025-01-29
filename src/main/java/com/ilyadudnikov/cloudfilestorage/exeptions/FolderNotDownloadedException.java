package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FolderNotDownloadedException extends RuntimeException {
    public FolderNotDownloadedException(String message) {
        super(message);
    }
}
