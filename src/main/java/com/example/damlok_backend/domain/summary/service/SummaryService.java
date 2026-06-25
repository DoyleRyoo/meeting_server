package com.example.damlok_backend.domain.summary.service;

import com.example.damlok_backend.domain.meeting.entity.Meeting;
import com.example.damlok_backend.domain.meeting.repository.MeetingRepository;
import com.example.damlok_backend.domain.summary.dto.SummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.SummaryResponseDto;
import com.example.damlok_backend.domain.summary.entity.Summary;
import com.example.damlok_backend.domain.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final MeetingRepository meetingRepository;

    // ✅ 1. 전체 요약 수정 (FULL)
    public void updateFullSummary(Long meetingId, SummaryUpdateRequestDto dto) {

        Summary summary = summaryRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의 요약입니다."));

        summary.setObjective(dto.getObjective());
        summary.setDiscussion(dto.getDiscussion());
        summary.setDecision(dto.getDecision());

        summaryRepository.save(summary);
    }

    // ✅ 2. 한 줄 요약 수정 (SHORT)
    public Long updateShortSummary(Long mid, ShortSummaryUpdateRequestDto dto) {

        Meeting meeting = meetingRepository.findById(mid)
                .orElseThrow(() -> new IllegalArgumentException("회의가 존재하지 않습니다."));

        meeting.setMeetingSummary(dto.getSummary());

        meetingRepository.save(meeting);

        return meeting.getId();
    }

    // ✅ 3. 전체 요약 조회 (FULL)
    public SummaryResponseDto getFullSummary(Long meetingId) {

    Summary summary = summaryRepository.findByMeetingId(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의 요약입니다."));

    return SummaryResponseDto.builder()
            .objective(summary.getObjective())
            .discussion(summary.getDiscussion())
            .decision(summary.getDecision())
            .build();
    }

    // ✅ 4. 한 줄 요약 조회 (SHORT)
    public ShortSummaryResponseDto getShortSummary(Long meetingId) {

    Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의입니다."));

    return ShortSummaryResponseDto.builder()
            .summary(meeting.getMeetingSummary())
            .build();
    }

}