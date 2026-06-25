package com.example.damlok_backend.domain.summary.controller;

import com.example.damlok_backend.domain.summary.dto.SummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.SummaryResponseDto;
import com.example.damlok_backend.domain.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/update")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    // ✅ 전체 요약 수정 (FULL)
    @PatchMapping("/full")
    public ResponseEntity<String> updateFullSummary(
            @RequestParam Long mid,
            @RequestBody SummaryUpdateRequestDto dto
    ) {

        summaryService.updateFullSummary(mid, dto);

        return ResponseEntity.ok("전체 요약 수정 완료");
    }

    // ✅ 한 줄 요약 수정 (SHORT)
    @PatchMapping("/short")
    public ResponseEntity<Long> updateShortSummary(
            @RequestParam Long mid,
            @RequestBody ShortSummaryUpdateRequestDto dto
    ) {

        Long meetingId = summaryService.updateShortSummary(mid, dto);

        return ResponseEntity.ok(meetingId);
    }
    
    // ✅ 전체 요약 조회 (FULL)
    @GetMapping("/full")
    public ResponseEntity<SummaryResponseDto> getFullSummary(
            @RequestParam Long mid
    ) {

        return ResponseEntity.ok(
                summaryService.getFullSummary(mid)
        );
    }

    // ✅ 한 줄 요약 조회 (SHORT)
    @GetMapping("/short")
    public ResponseEntity<ShortSummaryResponseDto> getShortSummary(
            @RequestParam Long mid
    ) {

        return ResponseEntity.ok(
                summaryService.getShortSummary(mid)
        );
    }
}