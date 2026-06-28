package com.example.damlok_backend.domain.meeting.entity;

import com.example.damlok_backend.domain.project.entity.Project;
import com.example.damlok_backend.global.entity.BaseEntity;
import com.example.damlok_backend.domain.participant.entity.MeetingParticipant;
import com.example.damlok_backend.domain.actionitem.entity.ActionItem;
import com.example.damlok_backend.domain.meeting.enums.MeetingStatus;
import com.example.damlok_backend.domain.summary.entity.Summary;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "meeting")
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "meeting_title", nullable = false)
    private String title;

    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime meetingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_status", nullable = false)
    private MeetingStatus status;

    @Column(name = "meeting_duration_sec")
    private Long durationSec;

    @Column(name = "meeting_started_at")
    private LocalDateTime startedAt;

    @Column(name = "meeting_ended_at")
    private LocalDateTime endedAt;

    @Lob
    @Column(name = "meeting_transcript")
    private String transcript;

    @Lob
    @Column(name = "meeting_cleaned_transcript")
    private String cleanedTranscript;

    @Column(name = "meeting_audio_url")
    private String audioUrl;

    @Column(name = "meeting_short_summary")
    private String shortSummary;

    @Column(name = "meeting_embedding", columnDefinition = "vector")
    private String embedding;

    @OneToMany(mappedBy = "meeting")
    private List<MeetingParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting")
    private List<ActionItem> actionItems = new ArrayList<>();

    @OneToOne(mappedBy = "meeting", cascade = CascadeType.ALL)
    private Summary summary;

    public void updateShortSummary(String shortSummary) {
        this.shortSummary = shortSummary;
    }
}
