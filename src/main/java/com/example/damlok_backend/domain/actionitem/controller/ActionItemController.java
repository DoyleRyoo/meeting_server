package com.example.damlok_backend.domain.actionitem.controller;

import com.example.damlok_backend.domain.actionitem.dto.ActionItemResponseDto;
import com.example.damlok_backend.domain.actionitem.dto.ActionItemUpdateRequestDto;
import com.example.damlok_backend.domain.actionitem.service.ActionItemService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/update")
@RequiredArgsConstructor
public class ActionItemController {

    private final ActionItemService actionItemService;

    @PatchMapping("/action")
    public ResponseEntity<String> updateActionItem(
            @RequestParam Long mid,
            @RequestBody ActionItemUpdateRequestDto dto
    ) {

        actionItemService.updateActionItem(mid, dto);

        return ResponseEntity.ok("action item 수정 완료");
    }

    // 프로젝트 회의록 action item 조회
    @GetMapping("/action")
    public ResponseEntity<List<ActionItemResponseDto>> getActionItems(
            @RequestParam Long mid
    ) {

        return ResponseEntity.ok(
                actionItemService.getActionItems(mid)
        );
    }
}