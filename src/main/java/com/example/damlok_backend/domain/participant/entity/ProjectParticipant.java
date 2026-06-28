package com.example.damlok_backend.domain.participant.entity;

import com.example.damlok_backend.domain.participant.enums.ProjectMemberGrade;
import com.example.damlok_backend.domain.participant.enums.ProjectMemberRole;
import com.example.damlok_backend.domain.participant.enums.ProjectMemberStatus;
import com.example.damlok_backend.domain.project.entity.Project;
import com.example.damlok_backend.domain.user.entity.User;
import com.example.damlok_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_participant")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ProjectParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_member_role", nullable = false)
    private ProjectMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectMemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_member_grade", nullable = false)
    private ProjectMemberGrade grade;

    public void updateStatus(ProjectMemberStatus status) {
        this.status = status;
    }

    public void updateRole(ProjectMemberRole role) {
        this.role = role;
    }

    public void updateGrade(ProjectMemberGrade grade) {
        this.grade = grade;
    }
}
