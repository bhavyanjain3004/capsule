package com.capsule.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SqsService {

    private static final Logger log = LoggerFactory.getLogger(SqsService.class);
    private final SqsClient sqsClient;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public void enqueue(Long capsuleId, Instant unlockAt) {
        long secondsUntilUnlock = ChronoUnit.SECONDS.between(Instant.now(), unlockAt);
        // SQS max delay is 15 minutes (900 seconds)
        int delay = (int) Math.min(900, Math.max(0, secondsUntilUnlock));

        log.info("Enqueuing capsule {} with delay {}s", capsuleId, delay);

        sqsClient.sendMessage(SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(capsuleId.toString())
            .delaySeconds(delay)
            .build());
    }

    public List<Message> receiveMessages() {
        return sqsClient.receiveMessage(ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .waitTimeSeconds(20)
            .build()).messages();
    }

    public void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(builder -> builder
            .queueUrl(queueUrl)
            .receiptHandle(receiptHandle));
    }
}
