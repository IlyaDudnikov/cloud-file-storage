package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FolderNotDeletedException extends RuntimeException {
    public FolderNotDeletedException(String message) {
        super(message);
    }
}
