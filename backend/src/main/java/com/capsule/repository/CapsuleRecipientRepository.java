package com.capsule.repository;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapsuleRecipientRepository extends JpaRepository<CapsuleRecipient, Long> {

    Optional<CapsuleRecipient> findByCapsuleAndEmail(Capsule capsule, String email);

    List<CapsuleRecipient> findByCapsule(Capsule capsule);
}
