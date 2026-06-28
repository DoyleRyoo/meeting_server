package com.example.damlok_backend.domain.summary.dto;

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
public class NotionActionItemUploadResultDto {

    private Long actionItemId;
    private String notionPageId;
    private String notionPageUrl;
}
