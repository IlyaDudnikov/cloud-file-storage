package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FileNotDownloadedException extends RuntimeException {
    public FileNotDownloadedException(String message) {
        super(message);
    }
}
