package com.capsule.service;

import java.time.Instant;

public interface SqsService {
    void enqueue(Long capsuleId, Instant unlockAt);
}
