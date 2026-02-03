package com.teamforge.backend.exception;

public class DotaProfileAlreadyExistsException extends RuntimeException {
    public DotaProfileAlreadyExistsException(String message) {
        super(message);
    }
}
