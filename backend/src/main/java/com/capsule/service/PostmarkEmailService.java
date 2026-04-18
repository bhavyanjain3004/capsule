package com.capsule.service;

import com.capsule.model.Capsule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostmarkEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(PostmarkEmailService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${postmark.api-token:MOCK_TOKEN}")
    private String apiToken;

    @Value("${postmark.from-email:hello@capsule.com}")
    private String fromEmail;

    @Value("${capsule.base-url}")
    private String baseUrl;

    @Override
    public void sendRecoveryEmail(String email, List<Capsule> capsules) {
        if (apiToken.equals("MOCK_TOKEN")) {
            log.warn("Postmark API token not set. Recovery email for {} suppressed.", email);
            log.info("Capsules: {}", capsules.stream().map(c -> c.getToken().toString()).collect(Collectors.joining(", ")));
            return;
        }

        String url = "https://api.postmarkapp.com/email";

        Map<String, Object> body = new HashMap<>();
        body.put("From", fromEmail);
        body.put("To", email);
        body.put("Subject", "Recover Your Capsules");
        
        StringBuilder htmlBody = new StringBuilder("<h1>Your Capsules</h1><ul>");
        for (Capsule c : capsules) {
            String link = baseUrl + "/capsule/" + c.getToken();
            htmlBody.append(String.format("<li><a href='%s'>%s</a> (Unlocks at: %s)</li>", 
                link, c.getTitle() != null ? c.getTitle() : "Untitled Capsule", c.getUnlockAt()));
        }
        htmlBody.append("</ul>");

        body.put("HtmlBody", htmlBody.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Postmark-Server-Token", apiToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(url, entity, String.class);
            log.info("Recovery email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send recovery email to {}", email, e);
        }
    }
}
