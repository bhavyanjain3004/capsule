package com.capsule.repository;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleDoodle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapsuleDoodleRepository extends JpaRepository<CapsuleDoodle, Long> {

    List<CapsuleDoodle> findByCapsule(Capsule capsule);

    void deleteByCapsule(Capsule capsule);
}
