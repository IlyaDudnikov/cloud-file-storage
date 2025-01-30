package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FolderNotCreatedException extends RuntimeException {
    public FolderNotCreatedException(String message) {
        super(message);
    }
}
