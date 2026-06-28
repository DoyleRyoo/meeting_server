package com.example.damlok_backend.domain.summary.entity;

import com.example.damlok_backend.domain.meeting.entity.Meeting;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "summary")
@EntityListeners(AuditingEntityListener.class)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Lob
    @Column(name = "summary_objective", nullable = false)
    private String objective;

    @Lob
    @Column(name = "summary_discussion", nullable = false)
    private String discussion;

    @Lob
    @Column(name = "summary_decision", nullable = false)
    private String decision;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateContent(String objective, String discussion, String decision) {
        this.objective = objective;
        this.discussion = discussion;
        this.decision = decision;
    }
}
