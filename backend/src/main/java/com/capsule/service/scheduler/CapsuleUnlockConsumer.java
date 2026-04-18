package com.capsule.service.scheduler;

import com.capsule.model.CapsuleStatus;
import com.capsule.repository.CapsuleRepository;
import com.capsule.service.CapsuleStateMachine;
import com.capsule.service.SqsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Component
public class CapsuleUnlockConsumer {

    private static final Logger log = LoggerFactory.getLogger(CapsuleUnlockConsumer.class);
    private final SqsService sqsService;
    private final CapsuleRepository capsuleRepo;
    private final CapsuleStateMachine stateMachine;

    public CapsuleUnlockConsumer(SqsService sqsService, CapsuleRepository capsuleRepo, CapsuleStateMachine stateMachine) {
        this.sqsService = sqsService;
        this.capsuleRepo = capsuleRepo;
        this.stateMachine = stateMachine;
    }

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<Message> messages = sqsService.receiveMessages();
        for (Message msg : messages) {
            try {
                Long capsuleId = Long.parseLong(msg.body());
                log.info("Processing unlock for capsule {}", capsuleId);

                capsuleRepo.findById(capsuleId).ifPresent(capsule -> {
                    if (capsule.getStatus() == CapsuleStatus.SEALED) {
                        stateMachine.transition(capsule, CapsuleStatus.UNLOCKED);
                        capsuleRepo.save(capsule);
                        log.info("Capsule {} is now UNLOCKED", capsuleId);
                    }
                });

                sqsService.deleteMessage(msg.receiptHandle());
            } catch (Exception e) {
                log.error("Failed to process message {}: {}", msg.messageId(), e.getMessage());
            }
        }
    }
}
