package com.example.damlok_backend.domain.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionCandidateDto {

    @JsonProperty("assignee_name")
    private String assigneeName;

    @JsonProperty("assignee_email")
    private String assigneeEmail;

    private String task;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    private String priority;
    private String status;

    @JsonProperty("source_type")
    private String sourceType;

    private String confidence;
    private String evidence;

    @JsonProperty("duplicate_group_id")
    private String duplicateGroupId;
}
