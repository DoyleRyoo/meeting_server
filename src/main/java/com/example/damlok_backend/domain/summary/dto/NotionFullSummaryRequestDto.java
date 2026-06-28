package com.example.damlok_backend.domain.summary.dto;

import java.time.LocalDateTime;

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
public class NotionFullSummaryRequestDto {

    private Long meetingId;
    private String meetingTitle;
    private LocalDateTime meetingDate;
    private String meetingSummary;
    private String objective;
    private String discussion;
    private String decision;
    private String notionPageId;
}
