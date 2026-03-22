package com.dala.crm.service;

import com.dala.crm.dto.AiProviderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Lightweight client for delegating summarize and draft calls to the Python AI service.
 */
@Component
public class AiGatewayClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final boolean enabled;
    private final String baseUrl;
    private final Duration timeout;

    public AiGatewayClient(
            ObjectMapper objectMapper,
            @Value("${ai.service.enabled:true}") boolean enabled,
            @Value("${ai.service.base-url:http://localhost:8090}") String baseUrl,
            @Value("${ai.service.timeout-ms:3000}") long timeoutMs
    ) {
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.timeout = Duration.ofMillis(timeoutMs);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    public Optional<AiProviderResponse> summarize(String text, int maxSentences) {
        return post("/v1/summarize", Map.of(
                "text", text,
                "max_sentences", maxSentences
        ));
    }

    public Optional<AiProviderResponse> draft(String intent, String context, String tone) {
        return post("/v1/draft", Map.of(
                "intent", intent,
                "context", context,
                "tone", tone
        ));
    }

    public Optional<AiProviderResponse> chat(
            String tenantId,
            String tenantName,
            String companyContext,
            List<Map<String, String>> conversation,
            String message
    ) {
        return post("/v1/chat", Map.of(
                "tenant_id", tenantId,
                "tenant_name", tenantName,
                "company_context", companyContext,
                "conversation", conversation,
                "message", message
        ));
    }

    private Optional<AiProviderResponse> post(String path, Object payload) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(timeout)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }
            return Optional.ofNullable(objectMapper.readValue(response.body(), AiProviderResponse.class));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
