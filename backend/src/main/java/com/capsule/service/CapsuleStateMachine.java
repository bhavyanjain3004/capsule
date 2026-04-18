package com.capsule.service;

import com.capsule.exception.InvalidStateTransitionException;
import com.capsule.model.Capsule;
import com.capsule.model.CapsuleStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.capsule.model.CapsuleStatus.*;

@Component
public class CapsuleStateMachine {

    // Define allowed transitions as a static Map<CapsuleStatus, Set<CapsuleStatus>>
    // Built once at class load, never mutated
    private static final Map<CapsuleStatus, Set<CapsuleStatus>> ALLOWED = Map.of(
        CapsuleStatus.SEALED,   Set.of(UNLOCKED, DELETED, EXPIRED),
        CapsuleStatus.UNLOCKED, Set.of(OPENED, DELETED),
        CapsuleStatus.OPENED,   Set.of(),
        CapsuleStatus.EXPIRED,  Set.of(),
        CapsuleStatus.DELETED,  Set.of()
    );

    /**
     * Validates and applies a status transition to a capsule.
     * Mutates capsule.status on success.
     * Throws InvalidStateTransitionException on invalid transition.
     */
    public void transition(Capsule capsule, CapsuleStatus to) {
        CapsuleStatus from = capsule.getStatus();
        if (!isValidTransition(from, to)) {
            throw new InvalidStateTransitionException(from, to);
        }
        capsule.setStatus(to);
    }

    private boolean isValidTransition(CapsuleStatus from, CapsuleStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }
}
