package com.example.damlok_backend.domain.actionitem.dto;

import com.example.damlok_backend.domain.actionitem.enums.ActionItemStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionItemUpdateRequestDto {
    private Long actionItemId;
    private String task;
    private ActionItemStatus status;
}
