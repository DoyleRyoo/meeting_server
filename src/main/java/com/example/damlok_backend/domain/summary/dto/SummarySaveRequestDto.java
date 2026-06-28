package com.example.damlok_backend.domain.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummarySaveRequestDto {

    private SummaryContentDto summary;

    @JsonProperty("action_items")
    private List<ActionItemDto> actionItems;

    @JsonProperty("action_candidates")
    private List<ActionCandidateDto> actionCandidates;

    @JsonProperty("meeting_summary")
    private String meetingSummary;
}
