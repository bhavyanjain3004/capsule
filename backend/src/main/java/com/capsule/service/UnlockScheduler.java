package com.capsule.service;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleStatus;
import com.capsule.repository.CapsuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UnlockScheduler {

    private static final Logger log = LoggerFactory.getLogger(UnlockScheduler.class);

    private final CapsuleRepository capsuleRepository;
    private final SqsService sqsService;

    public UnlockScheduler(CapsuleRepository capsuleRepository, SqsService sqsService) {
        this.capsuleRepository = capsuleRepository;
        this.sqsService = sqsService;
    }

    /**
     * Look for sealed capsules that are within 15 minutes of unlocking and haven't been enqueued yet.
     * Also handles capsules that are already past their unlock time in case of worker downtime.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void scanAndEnqueue() {
        LocalDateTime window = LocalDateTime.now().plusMinutes(15);
        
        List<Capsule> pending = capsuleRepository.findByStatusAndUnlockAtBefore(
            CapsuleStatus.SEALED, window);

        for (Capsule capsule : pending) {
            // In a real system, we'd use a "last_enqueued_at" column to avoid duplicate SQS messages
            // For this design, we'll rely on SQS message deduplication or idempotent processing
            Instant unlockAt = capsule.getUnlockAt().toInstant(ZoneOffset.UTC);
            sqsService.enqueue(capsule.getId(), unlockAt);
        }
    }
}
