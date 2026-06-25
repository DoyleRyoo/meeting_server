package com.example.damlok_backend.domain.actionitem.service;

import com.example.damlok_backend.domain.actionitem.dto.ActionItemResponseDto;
import com.example.damlok_backend.domain.actionitem.dto.ActionItemUpdateRequestDto;
import com.example.damlok_backend.domain.actionitem.entity.ActionItem;
import com.example.damlok_backend.domain.actionitem.repository.ActionItemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;

    public void updateActionItem(Long mid, ActionItemUpdateRequestDto dto) {

        ActionItem actionItem = actionItemRepository.findById(dto.getActionItemId())
                .orElseThrow(() -> new IllegalArgumentException("ActionItem이 존재하지 않습니다."));

        // ✔ 해당 meeting 소속인지 검증
        if (!actionItem.getMeeting().getId().equals(mid)) {
            throw new IllegalArgumentException("해당 회의의 ActionItem이 아닙니다.");
        }

        actionItem.setTask(dto.getTask());
        actionItem.setStatus(dto.getStatus());

        actionItemRepository.save(actionItem);
    }

    // 프로젝트 회의록 action item 조회
    public List<ActionItemResponseDto> getActionItems(Long meetingId) {

    List<ActionItem> actionItems = actionItemRepository.findByMeetingId(meetingId);

    return actionItems.stream()
            .map(actionItem -> ActionItemResponseDto.builder()
                    .actionItemId(actionItem.getId())
                    .task(actionItem.getTask())
                    .startDate(actionItem.getStartDate())
                    .dueDate(actionItem.getDueDate())
                    .priority(actionItem.getPriority())
                    .status(actionItem.getStatus())
                    .build())
            .toList();
    }
}   