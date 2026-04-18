package com.capsule.service.scheduler;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleStatus;
import com.capsule.repository.CapsuleRepository;
import com.capsule.service.SqsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class DevUnlockScheduler {

    private static final Logger log = LoggerFactory.getLogger(DevUnlockScheduler.class);
    private final CapsuleRepository capsuleRepo;
    private final SqsService sqsService;

    public DevUnlockScheduler(CapsuleRepository capsuleRepo, SqsService sqsService) {
        this.capsuleRepo = capsuleRepo;
        this.sqsService = sqsService;
    }

    /**
     * Finds SEALED capsules that should be unlocked within the next 15 minutes
     * and enqueues them into SQS.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void scheduleUnlocks() {
        LocalDateTime window = LocalDateTime.now().plusMinutes(15);
        List<Capsule> pending = capsuleRepo.findByStatusAndUnlockAtBefore(
            CapsuleStatus.SEALED, window);

        if (!pending.isEmpty()) {
            log.info("Found {} capsules entering unlock window", pending.size());
            for (Capsule c : pending) {
                sqsService.enqueue(c.getId(), c.getUnlockAt().toInstant(ZoneOffset.UTC));
            }
        }
    }
}
