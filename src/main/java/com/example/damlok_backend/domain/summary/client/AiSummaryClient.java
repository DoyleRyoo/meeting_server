package com.example.damlok_backend.domain.summary.client;

import com.example.damlok_backend.domain.summary.dto.AiSummaryRequestDto;
import com.example.damlok_backend.domain.summary.dto.AiSummaryResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class AiSummaryClient {

    private static final String SUMMARY_PATH = "/aiactions/summary";

    private final RestTemplate restTemplate;
    private final String aiServerBaseUrl;

    public AiSummaryClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${ai.server.base-url}") String aiServerBaseUrl
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(60))
                .build();
        this.aiServerBaseUrl = aiServerBaseUrl;
    }

    public AiSummaryResponseDto requestSummary(AiSummaryRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiSummaryRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<AiSummaryResponseDto> response = restTemplate.exchange(
                    buildSummaryUrl(),
                    HttpMethod.POST,
                    entity,
                    AiSummaryResponseDto.class
            );

            AiSummaryResponseDto body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null) {
                throw new IllegalStateException("AI summary server returned an empty or unsuccessful response.");
            }

            return body;
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "AI summary request failed with status " + exception.getStatusCode().value() + ".",
                    exception
            );
        } catch (ResourceAccessException exception) {
            throw new IllegalStateException("AI summary server is unreachable.", exception);
        } catch (RestClientException exception) {
            throw new IllegalStateException("AI summary request failed.", exception);
        }
    }

    private String buildSummaryUrl() {
        String normalizedBaseUrl = aiServerBaseUrl == null ? "" : aiServerBaseUrl.replaceAll("/+$", "");
        if (normalizedBaseUrl.isBlank()) {
            throw new IllegalStateException("AI server base URL is not configured.");
        }

        return normalizedBaseUrl + SUMMARY_PATH;
    }
}
