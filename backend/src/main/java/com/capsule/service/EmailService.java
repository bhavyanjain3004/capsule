package com.capsule.service;

import com.capsule.model.Capsule;
import java.util.List;

public interface EmailService {
    void sendRecoveryEmail(String email, List<Capsule> capsules);
}
