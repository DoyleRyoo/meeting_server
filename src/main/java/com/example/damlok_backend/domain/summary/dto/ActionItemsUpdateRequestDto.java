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
public class ActionItemsUpdateRequestDto {

    @JsonProperty("action_items")
    private List<ActionItemUpdateRequestDto> actionItems;
}
