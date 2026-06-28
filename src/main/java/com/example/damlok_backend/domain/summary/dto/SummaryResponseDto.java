package com.example.damlok_backend.domain.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryResponseDto {

    private Long meetingId;
    private SummaryContentDto summary;

    @JsonProperty("action_items")
    private List<ActionItemDto> actionItems;

    @JsonProperty("action_candidates")
    private List<ActionCandidateDto> actionCandidates;

    @JsonProperty("meeting_summary")
    private String meetingSummary;
}
