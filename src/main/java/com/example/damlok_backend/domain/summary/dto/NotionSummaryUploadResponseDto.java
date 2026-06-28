package com.example.damlok_backend.domain.summary.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotionSummaryUploadResponseDto {

    private Long meetingId;
    private String fullSummaryNotionPageId;
    private String fullSummaryNotionPageUrl;
    private int uploadedActionItemCount;

    @JsonProperty("action_item_notion_results")
    private List<NotionActionItemUploadResultDto> actionItemNotionResults;
}
