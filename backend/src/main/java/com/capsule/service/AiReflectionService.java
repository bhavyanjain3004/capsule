package com.capsule.service;

import com.capsule.model.Capsule;

public interface AiReflectionService {
    void generateReflectionIfAbsent(Capsule capsule, String content);
}
