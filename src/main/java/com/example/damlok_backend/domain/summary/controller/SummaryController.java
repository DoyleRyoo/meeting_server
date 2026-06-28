package com.example.damlok_backend.domain.summary.controller;

import com.example.damlok_backend.domain.summary.dto.AiSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.ActionItemsUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.NotionSummaryUploadResponseDto;
import com.example.damlok_backend.domain.summary.dto.SavedActionItemResponseDto;
import com.example.damlok_backend.domain.summary.dto.SavedFullSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.SavedShortSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.SummaryContentDto;
import com.example.damlok_backend.domain.summary.dto.SummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.SummarySaveRequestDto;
import com.example.damlok_backend.domain.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping("/summary/ai")
    public AiSummaryResponseDto generateAiSummary(@RequestParam Long mid) {
        return summaryService.generateAiSummary(mid);
    }

    @PostMapping("/summary/save")
    public SummaryResponseDto saveAiSummary(
            @RequestParam Long mid,
            @RequestBody SummarySaveRequestDto request
    ) {
        return summaryService.saveAiSummary(mid, request);
    }

    @PostMapping("/summary/notion")
    public NotionSummaryUploadResponseDto uploadSavedSummaryToNotion(@RequestParam Long mid) {
        return summaryService.uploadSavedSummaryToNotion(mid);
    }

    @GetMapping("/full")
    public SavedFullSummaryResponseDto getSummary(@RequestParam Long mid) {
        return summaryService.getSummary(mid);
    }

    @GetMapping("/short")
    public SavedShortSummaryResponseDto getShortSummary(@RequestParam Long mid) {
        return summaryService.getShortSummary(mid);
    }

    @GetMapping("/action")
    public List<SavedActionItemResponseDto> getActionItems(@RequestParam Long mid) {
        return summaryService.getActionItems(mid);
    }

    @PatchMapping("/update/full")
    public SavedFullSummaryResponseDto updateSummary(
            @RequestParam Long mid,
            @RequestBody SummaryContentDto request
    ) {
        return summaryService.updateSummary(mid, request);
    }

    @PatchMapping("/update/short")
    public SavedShortSummaryResponseDto updateShortSummary(
            @RequestParam Long mid,
            @RequestBody ShortSummaryUpdateRequestDto request
    ) {
        return summaryService.updateShortSummary(mid, request);
    }

    @PatchMapping("/update/action")
    public List<SavedActionItemResponseDto> updateActionItems(
            @RequestParam Long mid,
            @RequestBody ActionItemsUpdateRequestDto request
    ) {
        return summaryService.updateActionItems(mid, request);
    }
}
