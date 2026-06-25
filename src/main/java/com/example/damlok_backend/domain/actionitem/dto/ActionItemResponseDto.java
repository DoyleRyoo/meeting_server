package com.example.damlok_backend.domain.actionitem.dto;

import com.example.damlok_backend.domain.actionitem.enums.ActionItemPriority;
import com.example.damlok_backend.domain.actionitem.enums.ActionItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ActionItemResponseDto {

    private Long actionItemId;

    private String task;

    private LocalDate startDate;

    private LocalDate dueDate;

    private ActionItemPriority priority;

    private ActionItemStatus status;
}