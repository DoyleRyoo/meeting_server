package com.example.damlok_backend.domain.summary.repository;

import com.example.damlok_backend.domain.summary.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findByMeetingId(Long meetingId);
}
