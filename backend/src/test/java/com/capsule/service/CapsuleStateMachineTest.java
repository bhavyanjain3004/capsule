package com.capsule.service;

import com.capsule.exception.InvalidStateTransitionException;
import com.capsule.model.Capsule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.capsule.model.CapsuleStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CapsuleStateMachineTest {

    private CapsuleStateMachine stateMachine;
    private Capsule capsule;

    @BeforeEach
    void setUp() {
        stateMachine = new CapsuleStateMachine();
        capsule = new Capsule();
    }

    @Test
    @DisplayName("Valid transitions from SEALED")
    void testValidTransitionsFromSealed() {
        // SEALED -> UNLOCKED
        capsule.setStatus(SEALED);
        stateMachine.transition(capsule, UNLOCKED);
        assertEquals(UNLOCKED, capsule.getStatus());

        // SEALED -> DELETED
        capsule.setStatus(SEALED);
        stateMachine.transition(capsule, DELETED);
        assertEquals(DELETED, capsule.getStatus());

        // SEALED -> EXPIRED
        capsule.setStatus(SEALED);
        stateMachine.transition(capsule, EXPIRED);
        assertEquals(EXPIRED, capsule.getStatus());
    }

    @Test
    @DisplayName("Valid transitions from UNLOCKED")
    void testValidTransitionsFromUnlocked() {
        // UNLOCKED -> OPENED
        capsule.setStatus(UNLOCKED);
        stateMachine.transition(capsule, OPENED);
        assertEquals(OPENED, capsule.getStatus());

        // UNLOCKED -> DELETED
        capsule.setStatus(UNLOCKED);
        stateMachine.transition(capsule, DELETED);
        assertEquals(DELETED, capsule.getStatus());
    }

    @Test
    @DisplayName("Invalid transitions - Status should not change and exception thrown")
    void testInvalidTransitions() {
        // OPENED -> SEALED
        capsule.setStatus(OPENED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, SEALED));

        // OPENED -> UNLOCKED
        capsule.setStatus(OPENED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, UNLOCKED));

        // DELETED -> SEALED
        capsule.setStatus(DELETED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, SEALED));

        // EXPIRED -> SEALED
        capsule.setStatus(EXPIRED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, SEALED));

        // UNLOCKED -> SEALED
        capsule.setStatus(UNLOCKED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, SEALED));

        // SEALED -> OPENED (must go through UNLOCKED first)
        capsule.setStatus(SEALED);
        assertThrows(InvalidStateTransitionException.class, () -> stateMachine.transition(capsule, OPENED));
    }
}
