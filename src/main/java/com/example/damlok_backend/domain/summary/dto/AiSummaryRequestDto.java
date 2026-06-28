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
public class AiSummaryRequestDto {

    @JsonProperty("meeting_id")
    private Long meetingId;

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("meeting_title")
    private String meetingTitle;

    @JsonProperty("meeting_audio_url")
    private String meetingAudioUrl;

    @JsonProperty("meeting_transcript")
    private String meetingTranscript;

    @JsonProperty("meeting_cleaned_transcript")
    private String meetingCleanedTranscript;

    private List<ParticipantInfoDto> participants;
}
