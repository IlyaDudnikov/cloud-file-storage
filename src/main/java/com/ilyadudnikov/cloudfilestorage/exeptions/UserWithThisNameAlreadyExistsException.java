package com.ilyadudnikov.cloudfilestorage.exeptions;

public class UserWithThisNameAlreadyExistsException extends RuntimeException {
    public UserWithThisNameAlreadyExistsException(String message) {
        super(message);
    }
}
