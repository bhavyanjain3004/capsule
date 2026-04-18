package com.capsule.config;

import com.capsule.dto.ErrorResponse;
import com.capsule.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CapsuleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CapsuleNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse("Capsule not found", "NOT_FOUND"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(new ErrorResponse("Access denied", "ACCESS_DENIED"));
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateTransitionException ex) {
        return ResponseEntity.status(409).body(new ErrorResponse(ex.getMessage(), "INVALID_STATE"));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(429).body(new ErrorResponse("Too many requests", "RATE_LIMITED"));
    }

    @ExceptionHandler(StorageQuotaExceededException.class)
    public ResponseEntity<ErrorResponse> handleQuota(StorageQuotaExceededException ex) {
        return ResponseEntity.status(413).body(new ErrorResponse("Storage quota exceeded", "QUOTA_EXCEEDED"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(new ErrorResponse(message, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(new ErrorResponse("Something went wrong", "INTERNAL_ERROR"));
    }
}
