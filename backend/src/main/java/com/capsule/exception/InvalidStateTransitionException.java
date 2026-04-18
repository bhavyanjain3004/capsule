package com.capsule.exception;

import com.capsule.model.CapsuleStatus;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(CapsuleStatus from, CapsuleStatus to) {
        super("Invalid transition from " + from + " to " + to);
    }
}
