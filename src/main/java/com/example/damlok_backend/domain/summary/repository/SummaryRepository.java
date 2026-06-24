package com.example.damlok_backend.domain.summary.repository;

import com.example.damlok_backend.domain.summary.entity.Summary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findByMeetingId(Long meetingId);
}