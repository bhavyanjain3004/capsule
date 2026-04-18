package com.capsule.repository;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    Optional<Capsule> findByToken(UUID token);

    List<Capsule> findByCreatorEmail(String email);

    List<Capsule> findByStatusAndUnlockAtBeforeAndDeletedAtIsNull(CapsuleStatus status, LocalDateTime now);
}
