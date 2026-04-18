package com.capsule.exception;

public class CapsuleNotFoundException extends RuntimeException {
    public CapsuleNotFoundException(String message) {
        super(message);
    }
}
