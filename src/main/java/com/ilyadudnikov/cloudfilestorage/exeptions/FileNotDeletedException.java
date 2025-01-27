package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FileNotDeletedException extends RuntimeException {
    public FileNotDeletedException(String message) {
        super(message);
    }
}
