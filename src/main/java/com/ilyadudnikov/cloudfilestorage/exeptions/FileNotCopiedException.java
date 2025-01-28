package com.ilyadudnikov.cloudfilestorage.exeptions;

public class FileNotCopiedException extends RuntimeException {
    public FileNotCopiedException(String message) {
        super(message);
    }
}
