package com.capsule.service;

import com.capsule.model.Capsule;
import com.capsule.repository.CapsuleRepository;
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

@Service
@RequiredArgsConstructor
public class GeminiAiService implements AiReflectionService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);

    private final CapsuleRepository capsuleRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api-key:MOCK_KEY}")
    private String apiKey;

    public GeminiAiService(CapsuleRepository capsuleRepository) {
        this.capsuleRepository = capsuleRepository;
    }

    @Override
    public void generateReflectionIfAbsent(Capsule capsule, String content) {
        if (capsule.getAiReflection() != null) return;
        if (apiKey.equals("MOCK_KEY")) {
            log.warn("Gemini API key not set. Skipping AI reflection for capsule {}.", capsule.getId());
            return;
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String prompt = "You are an empathetic assistant help people write a 'letter to their future self'. " +
                        "Based on the following content extracted from an encrypted time capsule, generate a moving, 3-sentence reflection " +
                        "that the creator would appreciate reading when they unlock this years later. " +
                        "Content: " + content;

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", List.of(part));
        requestBody.put("contents", List.of(contentMap));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> resContent = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) resContent.get("parts");
                    if (!parts.isEmpty()) {
                        String reflection = (String) parts.get(0).get("text");
                        capsule.setAiReflection(reflection.trim());
                        capsuleRepository.save(capsule);
                        log.info("AI reflection generated for capsule {}", capsule.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate AI reflection for capsule {}", capsule.getId(), e);
        }
    }
}
