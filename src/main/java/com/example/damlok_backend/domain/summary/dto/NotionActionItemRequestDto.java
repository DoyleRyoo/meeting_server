package com.example.damlok_backend.domain.summary.dto;

import java.time.LocalDate;

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
public class NotionActionItemRequestDto {

    private Long meetingId;
    private String meetingTitle;
    private Long actionItemId;
    private String assigneeName;
    private String assigneeEmail;
    private String task;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private String notionPageId;
}
