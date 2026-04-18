package com.capsule.exception;

public class StorageQuotaExceededException extends RuntimeException {
    public StorageQuotaExceededException() {
        super("Storage quota exceeded");
    }
    public StorageQuotaExceededException(String message) {
        super(message);
    }
}
