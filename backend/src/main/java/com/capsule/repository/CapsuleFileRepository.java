package com.capsule.repository;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapsuleFileRepository extends JpaRepository<CapsuleFile, Long> {

    List<CapsuleFile> findByCapsule(Capsule capsule);

    @Query("SELECT COALESCE(SUM(f.fileSizeKb), 0) FROM CapsuleFile f WHERE f.capsule.id = :capsuleId")
    Integer sumFileSizeKbByCapsuleId(@Param("capsuleId") Long capsuleId);
}
