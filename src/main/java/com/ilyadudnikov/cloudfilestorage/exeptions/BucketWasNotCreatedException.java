package com.ilyadudnikov.cloudfilestorage.exeptions;

public class BucketWasNotCreatedException extends RuntimeException {
    public BucketWasNotCreatedException(String message) {
        super(message);
    }
}
