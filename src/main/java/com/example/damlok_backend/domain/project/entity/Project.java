package com.example.damlok_backend.domain.project.entity;

import com.example.damlok_backend.domain.company.entity.Company;
import com.example.damlok_backend.global.entity.BaseEntity;
import com.example.damlok_backend.domain.meeting.entity.Meeting;
import com.example.damlok_backend.domain.participant.entity.ProjectParticipant;
import com.example.damlok_backend.domain.project.enums.ProjectStatus;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "project_title", nullable = false)
    private String title;

    @Lob
    @Column(name = "project_description")
    private String description;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus status;

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "project")
    private List<ProjectParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Meeting> meetings = new ArrayList<>();
}
