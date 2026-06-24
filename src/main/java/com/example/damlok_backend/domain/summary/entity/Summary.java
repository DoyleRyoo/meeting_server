package com.example.damlok_backend.domain.summary.entity;

import com.example.damlok_backend.domain.meeting.entity.Meeting;
import com.example.damlok_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Lob
    private String objective;

    @Lob
    private String discussion;

    @Lob
    private String decision;
}