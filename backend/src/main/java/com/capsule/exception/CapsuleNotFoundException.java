package com.capsule.exception;

public class CapsuleNotFoundException extends RuntimeException {
    public CapsuleNotFoundException() {
        super("Capsule not found");
    }
}
