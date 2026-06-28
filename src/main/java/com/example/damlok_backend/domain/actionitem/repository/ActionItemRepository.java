package com.example.damlok_backend.domain.actionitem.repository;

import com.example.damlok_backend.domain.actionitem.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {
    List<ActionItem> findAllByMeetingId(Long meetingId);

    Optional<ActionItem> findByIdAndMeetingId(Long id, Long meetingId);
}
