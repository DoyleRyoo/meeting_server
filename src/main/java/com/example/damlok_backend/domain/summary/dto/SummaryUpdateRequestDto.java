package com.example.damlok_backend.domain.summary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryUpdateRequestDto {

    private String objective;
    private String discussion;
    private String decision;
}