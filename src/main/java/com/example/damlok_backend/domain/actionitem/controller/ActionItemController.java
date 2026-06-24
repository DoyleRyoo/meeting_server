package com.example.damlok_backend.domain.actionitem.controller;

import com.example.damlok_backend.domain.actionitem.dto.ActionItemUpdateRequestDto;
import com.example.damlok_backend.domain.actionitem.service.ActionItemService;
import lombok.RequiredArgsConstructor;
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
}