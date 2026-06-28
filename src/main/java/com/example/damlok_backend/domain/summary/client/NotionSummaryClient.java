package com.example.damlok_backend.domain.summary.client;

import com.example.damlok_backend.domain.summary.dto.NotionActionItemRequestDto;
import com.example.damlok_backend.domain.summary.dto.NotionFullSummaryRequestDto;
import com.example.damlok_backend.domain.summary.dto.NotionUploadResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotionSummaryClient {

    private static final ParameterizedTypeReference<Map<String, Object>> NOTION_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;
    private final String notionApiBaseUrl;
    private final String notionToken;
    private final String notionVersion;
    private final String actionItemDatabaseId;

    public NotionSummaryClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${notion.api.base-url:https://api.notion.com/v1}") String notionApiBaseUrl,
            @Value("${notion.api.token:}") String notionToken,
            @Value("${notion.api.version:2022-06-28}") String notionVersion,
            @Value("${notion.action-item.database-id:}") String actionItemDatabaseId
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .build();
        this.notionApiBaseUrl = notionApiBaseUrl;
        this.notionToken = notionToken;
        this.notionVersion = notionVersion;
        this.actionItemDatabaseId = actionItemDatabaseId;
    }

    public NotionUploadResultDto uploadFullSummary(NotionFullSummaryRequestDto request) {
        String parentPageId = extractPageId(request.getNotionPageUrl());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("parent", Map.of("page_id", parentPageId));
        body.put("properties", buildFullSummaryProperties(request));
        body.put("children", buildFullSummaryChildren(request));

        return exchangeForUploadResult("/pages", HttpMethod.POST, body);
    }

    public NotionUploadResultDto uploadActionItem(NotionActionItemRequestDto request) {
        if (StringUtils.hasText(request.getNotionPageId())) {
            return updatePage(request.getNotionPageId(), buildActionItemProperties(request));
        }

        if (!StringUtils.hasText(actionItemDatabaseId)) {
            throw new IllegalStateException("Notion action item database id is not configured.");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("parent", Map.of("database_id", actionItemDatabaseId));
        body.put("properties", buildActionItemProperties(request));

        return exchangeForUploadResult("/pages", HttpMethod.POST, body);
    }

    private NotionUploadResultDto updatePage(String pageId, Map<String, Object> properties) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("properties", properties);

        return exchangeForUploadResult("/pages/" + pageId, HttpMethod.PATCH, body);
    }

    private Map<String, Object> buildFullSummaryProperties(NotionFullSummaryRequestDto request) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("title", titleProperty(nonBlank(request.getMeetingTitle(), "Meeting " + request.getMeetingId())));
        return properties;
    }

    private List<Map<String, Object>> buildFullSummaryChildren(NotionFullSummaryRequestDto request) {
        List<Map<String, Object>> children = new ArrayList<>();
        children.add(headingBlock("Meeting Summary"));
        children.add(paragraphBlock("Meeting Date: " + formatValue(request.getMeetingDate())));
        children.add(paragraphBlock("One-line Summary: " + formatValue(request.getMeetingSummary())));
        children.add(headingBlock("Objective"));
        children.add(paragraphBlock(request.getObjective()));
        children.add(headingBlock("Discussion"));
        children.add(paragraphBlock(request.getDiscussion()));
        children.add(headingBlock("Decision"));
        children.add(paragraphBlock(request.getDecision()));
        return children;
    }

    private Map<String, Object> buildActionItemProperties(NotionActionItemRequestDto request) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("Name", titleProperty(nonBlank(request.getTask(), "Action Item " + request.getActionItemId())));
        properties.put("Meeting", richTextProperty(nonBlank(request.getMeetingTitle(), "Meeting " + request.getMeetingId())));
        properties.put("Assignee Name", richTextProperty(request.getAssigneeName()));
        properties.put("Assignee Email", emailProperty(request.getAssigneeEmail()));
        properties.put("Task", richTextProperty(request.getTask()));
        properties.put("Start Date", dateProperty(request.getStartDate() == null ? null : request.getStartDate().toString()));
        properties.put("Due Date", dateProperty(request.getDueDate() == null ? null : request.getDueDate().toString()));
        properties.put("Priority", selectProperty(request.getPriority()));
        properties.put("Status", selectProperty(request.getStatus()));
        return properties;
    }

    private NotionUploadResultDto exchangeForUploadResult(
            String path,
            HttpMethod method,
            Map<String, Object> body
    ) {
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, buildHeaders());

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    buildUrl(path),
                    method,
                    entity,
                    NOTION_RESPONSE_TYPE
            );

            Map<String, Object> responseBody = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new IllegalStateException("Notion upload returned an empty or unsuccessful response.");
            }

            return NotionUploadResultDto.builder()
                    .notionPageId(asString(responseBody.get("id")))
                    .notionPageUrl(asString(responseBody.get("url")))
                    .build();
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "Notion upload failed with status " + exception.getStatusCode().value() + ".",
                    exception
            );
        } catch (ResourceAccessException exception) {
            throw new IllegalStateException("Notion API is unreachable.", exception);
        } catch (RestClientException exception) {
            throw new IllegalStateException("Notion upload failed.", exception);
        }
    }

    private String extractPageId(String notionPageUrl) {
        if (!StringUtils.hasText(notionPageUrl)) {
            throw new IllegalStateException("Company Notion URL is not configured.");
        }

        String urlWithoutQuery = notionPageUrl.trim().split("[?#]", 2)[0];
        String pageId = urlWithoutQuery.replaceAll("[^A-Fa-f0-9]", "");
        if (pageId.length() < 32) {
            throw new IllegalStateException("Company Notion URL does not contain a valid page id.");
        }

        return pageId.substring(pageId.length() - 32);
    }

    private HttpHeaders buildHeaders() {
        if (!StringUtils.hasText(notionToken)) {
            throw new IllegalStateException("Notion API token is not configured.");
        }
        if (!StringUtils.hasText(notionVersion)) {
            throw new IllegalStateException("Notion API version is not configured.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(notionToken);
        headers.set("Notion-Version", notionVersion);
        return headers;
    }

    private String buildUrl(String path) {
        String normalizedBaseUrl = notionApiBaseUrl == null ? "" : notionApiBaseUrl.replaceAll("/+$", "");
        if (normalizedBaseUrl.isBlank()) {
            throw new IllegalStateException("Notion API base URL is not configured.");
        }

        return normalizedBaseUrl + path;
    }

    private Map<String, Object> titleProperty(String value) {
        return Map.of("title", List.of(textObject(value)));
    }

    private Map<String, Object> richTextProperty(String value) {
        return Map.of("rich_text", List.of(textObject(formatValue(value))));
    }

    private Map<String, Object> emailProperty(String value) {
        Map<String, Object> property = new LinkedHashMap<>();
        property.put("email", StringUtils.hasText(value) ? value : null);
        return property;
    }

    private Map<String, Object> dateProperty(String value) {
        Map<String, Object> property = new LinkedHashMap<>();
        property.put("date", StringUtils.hasText(value) ? Map.of("start", value) : null);
        return property;
    }

    private Map<String, Object> selectProperty(String value) {
        Map<String, Object> property = new LinkedHashMap<>();
        property.put("select", StringUtils.hasText(value) ? Map.of("name", value) : null);
        return property;
    }

    private Map<String, Object> headingBlock(String text) {
        return Map.of(
                "object", "block",
                "type", "heading_2",
                "heading_2", Map.of("rich_text", List.of(textObject(text)))
        );
    }

    private Map<String, Object> paragraphBlock(String text) {
        return Map.of(
                "object", "block",
                "type", "paragraph",
                "paragraph", Map.of("rich_text", List.of(textObject(formatValue(text))))
        );
    }

    private Map<String, Object> textObject(String value) {
        return Map.of("type", "text", "text", Map.of("content", formatValue(value)));
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof java.time.LocalDateTime localDateTime) {
            return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return value.toString();
    }

    private String nonBlank(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
