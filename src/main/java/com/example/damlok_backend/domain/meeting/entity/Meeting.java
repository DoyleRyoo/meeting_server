package com.example.damlok_backend.domain.meeting.entity;

import com.example.damlok_backend.domain.project.entity.Project;
import com.example.damlok_backend.global.entity.BaseEntity;
import com.example.damlok_backend.domain.participant.entity.MeetingParticipant;
import com.example.damlok_backend.domain.actionitem.entity.ActionItem;
import com.example.damlok_backend.domain.meeting.enums.MeetingStatus;
import com.example.damlok_backend.domain.summary.entity.Summary;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String title;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    private Long durationSec;

    @Lob
    private String transcript;

    @Lob
    private String cleanedTranscript;

    private String audioUrl;

    @Lob
    private String meetingSummary;

    @OneToMany(mappedBy = "meeting")
    private List<MeetingParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting")
    private List<ActionItem> actionItems = new ArrayList<>();

    @OneToOne(mappedBy = "meeting", cascade = CascadeType.ALL)
    private Summary summary;
}