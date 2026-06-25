package com.example.damlok_backend.domain.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SummaryResponseDto {

    private String objective;
    private String discussion;
    private String decision;
}