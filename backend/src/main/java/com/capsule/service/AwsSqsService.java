package com.capsule.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AwsSqsService implements SqsService {

    private static final Logger log = LoggerFactory.getLogger(AwsSqsService.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public AwsSqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public void enqueue(Long capsuleId, Instant unlockAt) {
        long delaySeconds = Duration.between(Instant.now(), unlockAt).toSeconds();
        
        // SQS max visibility delay is 15 minutes (900 seconds)
        if (delaySeconds > 900) {
            log.info("Capsule {} unlock time is > 15 mins away. Skipping SQS enqueue, UnlockScheduler will handle it.", capsuleId);
            return;
        }

        if (delaySeconds < 0) delaySeconds = 0;

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(capsuleId.toString())
                .delaySeconds((int) delaySeconds)
                .build();

        sqsClient.sendMessage(sendMsgRequest);
        log.info("Enqueued capsule {} in SQS with delay of {}s", capsuleId, delaySeconds);
    }
}
