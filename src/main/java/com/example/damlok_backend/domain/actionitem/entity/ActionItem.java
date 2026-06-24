package com.example.damlok_backend.domain.actionitem.entity;

import com.example.damlok_backend.domain.actionitem.enums.ActionItemPriority;
import com.example.damlok_backend.domain.actionitem.enums.ActionItemStatus;
import com.example.damlok_backend.domain.meeting.entity.Meeting;
import com.example.damlok_backend.domain.participant.entity.ProjectParticipant;
import com.example.damlok_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ActionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_participant_id")
    private ProjectParticipant assignee;

    @Lob
    private String task;

    private LocalDate startDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ActionItemPriority priority;

    @Enumerated(EnumType.STRING)
    private ActionItemStatus status;
}