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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "action_item")
public class ActionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_member_id", nullable = false)
    private ProjectParticipant assignee;

    @Column(name = "assignee_name")
    private String assigneeName;

    @Column(name = "assignee_email")
    private String assigneeEmail;

    @Lob
    @Column(name = "action_item_task", nullable = false)
    private String task;

    @Column(name = "action_item_start_date")
    private LocalDate startDate;

    @Column(name = "action_item_due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_item_priority", nullable = false)
    private ActionItemPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_item_status", nullable = false)
    private ActionItemStatus status;

    @Column(name = "action_item_notion_page_id")
    private String notionPageId;

    @Lob
    @Column(name = "action_item_notion_page_url")
    private String notionPageUrl;

    public void updateContent(
            ProjectParticipant assignee,
            String assigneeName,
            String assigneeEmail,
            String task,
            LocalDate startDate,
            LocalDate dueDate,
            ActionItemPriority priority,
            ActionItemStatus status
    ) {
        this.assignee = assignee;
        this.assigneeName = assigneeName;
        this.assigneeEmail = assigneeEmail;
        this.task = task;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public void updateNotionPage(String notionPageId, String notionPageUrl) {
        this.notionPageId = notionPageId;
        this.notionPageUrl = notionPageUrl;
    }
}
