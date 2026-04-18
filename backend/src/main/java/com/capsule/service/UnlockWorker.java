package com.capsule.service;

import com.capsule.model.Capsule;
import com.capsule.model.CapsuleStatus;
import com.capsule.repository.CapsuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnlockWorker {

    private static final Logger log = LoggerFactory.getLogger(UnlockWorker.class);

    private final SqsClient sqsClient;
    private final CapsuleRepository capsuleRepository;
    private final CapsuleStateMachine stateMachine;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public UnlockWorker(SqsClient sqsClient, CapsuleRepository capsuleRepository, CapsuleStateMachine stateMachine) {
        this.sqsClient = sqsClient;
        this.capsuleRepository = capsuleRepository;
        this.stateMachine = stateMachine;
    }

    @Scheduled(fixedRate = 10000) // Poll every 10 seconds
    public void pollQueue() {
        if (queueUrl == null || queueUrl.isEmpty()) return;

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20) // Long polling
                .build();

        try {
            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            for (Message message : messages) {
                processMessage(message);
            }
        } catch (Exception e) {
            log.error("Error polling SQS queue", e);
        }
    }

    private void processMessage(Message message) {
        try {
            Long capsuleId = Long.parseLong(message.body());
            Optional<Capsule> capsuleOpt = capsuleRepository.findById(capsuleId);

            if (capsuleOpt.isPresent()) {
                Capsule capsule = capsuleOpt.get();
                if (capsule.getStatus() == CapsuleStatus.SEALED) {
                    stateMachine.transition(capsule, CapsuleStatus.UNLOCKED);
                    capsuleRepository.save(capsule);
                    log.info("Capsule {} successfully unlocked via SQS worker", capsuleId);
                }
            }

            // Always delete the message after processing (or if the capsule is already unlocked)
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);

        } catch (Exception e) {
            log.error("Failed to process SQS message: {}", message.body(), e);
        }
    }
}
