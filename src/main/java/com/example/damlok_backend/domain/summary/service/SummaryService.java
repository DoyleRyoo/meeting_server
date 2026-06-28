package com.example.damlok_backend.domain.summary.service;

import com.example.damlok_backend.domain.actionitem.entity.ActionItem;
import com.example.damlok_backend.domain.actionitem.enums.ActionItemPriority;
import com.example.damlok_backend.domain.actionitem.enums.ActionItemStatus;
import com.example.damlok_backend.domain.actionitem.repository.ActionItemRepository;
import com.example.damlok_backend.domain.meeting.entity.Meeting;
import com.example.damlok_backend.domain.meeting.repository.MeetingRepository;
import com.example.damlok_backend.domain.participant.entity.ProjectParticipant;
import com.example.damlok_backend.domain.participant.enums.ProjectMemberStatus;
import com.example.damlok_backend.domain.participant.repository.ProjectParticipantRepository;
import com.example.damlok_backend.domain.project.entity.Project;
import com.example.damlok_backend.domain.summary.client.AiSummaryClient;
import com.example.damlok_backend.domain.summary.client.NotionSummaryClient;
import com.example.damlok_backend.domain.summary.dto.ActionItemDto;
import com.example.damlok_backend.domain.summary.dto.ActionItemUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.ActionItemsUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.AiSummaryRequestDto;
import com.example.damlok_backend.domain.summary.dto.AiSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.NotionActionItemRequestDto;
import com.example.damlok_backend.domain.summary.dto.NotionActionItemUploadResultDto;
import com.example.damlok_backend.domain.summary.dto.NotionFullSummaryRequestDto;
import com.example.damlok_backend.domain.summary.dto.NotionSummaryUploadResponseDto;
import com.example.damlok_backend.domain.summary.dto.NotionUploadResultDto;
import com.example.damlok_backend.domain.summary.dto.ParticipantInfoDto;
import com.example.damlok_backend.domain.summary.dto.SavedActionItemResponseDto;
import com.example.damlok_backend.domain.summary.dto.SavedFullSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.SavedShortSummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.ShortSummaryUpdateRequestDto;
import com.example.damlok_backend.domain.summary.dto.SummaryContentDto;
import com.example.damlok_backend.domain.summary.dto.SummaryResponseDto;
import com.example.damlok_backend.domain.summary.dto.SummarySaveRequestDto;
import com.example.damlok_backend.domain.summary.entity.Summary;
import com.example.damlok_backend.domain.summary.repository.SummaryRepository;
import com.example.damlok_backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private static final Map<String, ActionItemStatus> ACTION_STATUS_MAP = Map.of(
            "미착수", ActionItemStatus.TODO,
            "진행중", ActionItemStatus.IN_PROGRESS,
            "완료", ActionItemStatus.DONE
    );

    private final MeetingRepository meetingRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final SummaryRepository summaryRepository;
    private final ActionItemRepository actionItemRepository;
    private final AiSummaryClient aiSummaryClient;
    private final NotionSummaryClient notionSummaryClient;

    @Transactional(readOnly = true)
    public AiSummaryResponseDto generateAiSummary(Long meetingId) {
        if (meetingId == null) {
            throw new IllegalArgumentException("Meeting id is required.");
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found."));

        Project project = meeting.getProject();
        if (project == null || project.getId() == null) {
            throw new IllegalStateException("Meeting project information is missing.");
        }

        AiSummaryRequestDto request = AiSummaryRequestDto.builder()
                .meetingId(meeting.getId())
                .projectId(project.getId())
                .meetingTitle(meeting.getTitle())
                .meetingAudioUrl(meeting.getAudioUrl())
                .meetingTranscript(meeting.getTranscript())
                .meetingCleanedTranscript(meeting.getCleanedTranscript())
                .participants(getParticipantInfos(project.getId()))
                .build();

        AiSummaryResponseDto response = aiSummaryClient.requestSummary(request);
        validateResponse(response);

        return response;
    }

    @Transactional
    public SummaryResponseDto saveAiSummary(Long meetingId, SummarySaveRequestDto request) {
        if (meetingId == null) {
            throw new IllegalArgumentException("Meeting id is required.");
        }
        validateSaveRequest(request);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found."));

        Project project = meeting.getProject();
        if (project == null || project.getId() == null) {
            throw new IllegalStateException("Meeting project information is missing.");
        }

        Summary savedSummary = saveSummary(meeting, request.getSummary());
        meeting.updateShortSummary(request.getMeetingSummary().trim());

        List<ActionItem> actionItems = request.getActionItems()
                .stream()
                .map(actionItem -> toActionItem(meeting, project.getId(), actionItem))
                .toList();

        actionItemRepository.deleteAll(actionItemRepository.findAllByMeetingId(meeting.getId()));
        List<ActionItem> savedActionItems = actionItemRepository.saveAll(actionItems);

        return SummaryResponseDto.builder()
                .meetingId(meeting.getId())
                .summary(SummaryContentDto.builder()
                        .objective(savedSummary.getObjective())
                        .discussion(savedSummary.getDiscussion())
                        .decision(savedSummary.getDecision())
                        .build())
                .actionItems(savedActionItems.stream()
                        .map(this::toActionItemDto)
                        .toList())
                .actionCandidates(List.of())
                .meetingSummary(meeting.getShortSummary())
                .build();
    }

    @Transactional(readOnly = true)
    public SavedFullSummaryResponseDto getFullSummary(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        Summary summary = summaryRepository.findByMeetingId(meeting.getId())
                .orElseThrow(() -> new IllegalArgumentException("Full summary not found."));

        return toSavedFullSummaryResponse(meeting, summary);
    }

    @Transactional(readOnly = true)
    public SavedShortSummaryResponseDto getShortSummary(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);

        return toSavedShortSummaryResponse(meeting);
    }

    @Transactional(readOnly = true)
    public List<SavedActionItemResponseDto> getActionItems(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);

        return actionItemRepository.findAllByMeetingId(meeting.getId())
                .stream()
                .map(this::toSavedActionItemResponse)
                .toList();
    }

    @Transactional
    public SavedFullSummaryResponseDto updateFullSummary(Long meetingId, SummaryContentDto request) {
        validateFullSummaryUpdateRequest(request);

        Meeting meeting = getMeeting(meetingId);
        Summary summary = summaryRepository.findByMeetingId(meeting.getId())
                .orElseThrow(() -> new IllegalArgumentException("Full summary not found."));

        summary.updateContent(
                request.getObjective().trim(),
                request.getDiscussion().trim(),
                request.getDecision().trim()
        );

        return toSavedFullSummaryResponse(meeting, summary);
    }

    @Transactional
    public SavedShortSummaryResponseDto updateShortSummary(Long meetingId, ShortSummaryUpdateRequestDto request) {
        validateShortSummaryUpdateRequest(request);

        Meeting meeting = getMeeting(meetingId);
        meeting.updateShortSummary(request.getMeetingSummary().trim());

        return toSavedShortSummaryResponse(meeting);
    }

    @Transactional
    public List<SavedActionItemResponseDto> updateActionItems(Long meetingId, ActionItemsUpdateRequestDto request) {
        validateActionItemsUpdateRequest(request);

        Meeting meeting = getMeeting(meetingId);
        Project project = meeting.getProject();
        if (project == null || project.getId() == null) {
            throw new IllegalStateException("Meeting project information is missing.");
        }

        Set<Long> requestedActionItemIds = new HashSet<>();
        List<ActionItem> actionItems = request.getActionItems()
                .stream()
                .map(actionItem -> toUpdatedActionItem(meeting, project.getId(), requestedActionItemIds, actionItem))
                .toList();

        deleteOmittedActionItems(meeting.getId(), requestedActionItemIds);

        return actionItemRepository.saveAll(actionItems)
                .stream()
                .map(this::toSavedActionItemResponse)
                .toList();
    }

    @Transactional
    public NotionSummaryUploadResponseDto uploadSavedSummaryToNotion(Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        Summary summary = summaryRepository.findByMeetingId(meeting.getId())
                .orElseThrow(() -> new IllegalArgumentException("Full summary not found."));
        List<ActionItem> actionItems = actionItemRepository.findAllByMeetingId(meeting.getId());

        NotionUploadResultDto fullSummaryResult = notionSummaryClient.uploadFullSummary(
                toNotionFullSummaryRequest(meeting, summary)
        );

        List<ActionItemNotionUpload> actionItemUploads = new ArrayList<>();
        for (ActionItem actionItem : actionItems) {
            NotionUploadResultDto actionItemResult = notionSummaryClient.uploadActionItem(
                    toNotionActionItemRequest(meeting, actionItem)
            );
            actionItemUploads.add(new ActionItemNotionUpload(actionItem, actionItemResult));
        }

        actionItemUploads.forEach(upload -> upload.actionItem().updateNotionPage(
                upload.result().getNotionPageId(),
                upload.result().getNotionPageUrl()
        ));

        return NotionSummaryUploadResponseDto.builder()
                .meetingId(meeting.getId())
                .fullSummaryNotionPageId(fullSummaryResult.getNotionPageId())
                .fullSummaryNotionPageUrl(fullSummaryResult.getNotionPageUrl())
                .uploadedActionItemCount(actionItemUploads.size())
                .actionItemNotionResults(actionItemUploads.stream()
                        .map(this::toNotionActionItemUploadResult)
                        .toList())
                .build();
    }

    private Summary saveSummary(Meeting meeting, SummaryContentDto summaryContent) {
        return summaryRepository.findByMeetingId(meeting.getId())
                .map(summary -> {
                    summary.updateContent(
                            summaryContent.getObjective().trim(),
                            summaryContent.getDiscussion().trim(),
                            summaryContent.getDecision().trim()
                    );
                    return summary;
                })
                .orElseGet(() -> summaryRepository.save(Summary.builder()
                        .meeting(meeting)
                        .objective(summaryContent.getObjective().trim())
                        .discussion(summaryContent.getDiscussion().trim())
                        .decision(summaryContent.getDecision().trim())
                        .build()));
    }

    private ActionItem toUpdatedActionItem(
            Meeting meeting,
            Long projectId,
            Set<Long> requestedActionItemIds,
            ActionItemUpdateRequestDto request
    ) {
        validateActionItemUpdateRequest(request);

        ProjectParticipant assignee = resolveProjectParticipant(
                projectId,
                request.getAssigneeEmail(),
                request.getAssigneeName()
        );
        String assigneeName = trimToNull(request.getAssigneeName());
        String assigneeEmail = trimToNull(request.getAssigneeEmail());
        ActionItemPriority priority = parsePriority(request.getPriority());
        ActionItemStatus status = parseStatus(request.getStatus());

        if (request.getActionItemId() == null) {
            return ActionItem.builder()
                    .meeting(meeting)
                    .assignee(assignee)
                    .assigneeName(assigneeName)
                    .assigneeEmail(assigneeEmail)
                    .task(request.getTask().trim())
                    .startDate(request.getStartDate())
                    .dueDate(request.getDueDate())
                    .priority(priority)
                    .status(status)
                    .build();
        }

        if (!requestedActionItemIds.add(request.getActionItemId())) {
            throw new IllegalArgumentException("Duplicate action item id in update request.");
        }

        ActionItem actionItem = actionItemRepository.findByIdAndMeetingId(request.getActionItemId(), meeting.getId())
                .orElseThrow(() -> new IllegalArgumentException("Action item not found."));
        actionItem.updateContent(
                assignee,
                assigneeName,
                assigneeEmail,
                request.getTask().trim(),
                request.getStartDate(),
                request.getDueDate(),
                priority,
                status
        );

        return actionItem;
    }

    private void deleteOmittedActionItems(Long meetingId, Set<Long> requestedActionItemIds) {
        List<ActionItem> omittedActionItems = actionItemRepository.findAllByMeetingId(meetingId)
                .stream()
                .filter(actionItem -> !requestedActionItemIds.contains(actionItem.getId()))
                .toList();

        actionItemRepository.deleteAll(omittedActionItems);
    }

    private ActionItem toActionItem(
            Meeting meeting,
            Long projectId,
            ActionItemDto actionItem
    ) {
        return ActionItem.builder()
                .meeting(meeting)
                .assignee(resolveProjectParticipant(projectId, actionItem))
                .assigneeName(trimToNull(actionItem.getAssigneeName()))
                .assigneeEmail(trimToNull(actionItem.getAssigneeEmail()))
                .task(actionItem.getTask().trim())
                .startDate(actionItem.getStartDate())
                .dueDate(actionItem.getDueDate())
                .priority(parsePriority(actionItem.getPriority()))
                .status(parseStatus(actionItem.getStatus()))
                .build();
    }

    private ProjectParticipant resolveProjectParticipant(Long projectId, ActionItemDto actionItem) {
        return resolveProjectParticipant(projectId, actionItem.getAssigneeEmail(), actionItem.getAssigneeName());
    }

    private ProjectParticipant resolveProjectParticipant(Long projectId, String assigneeEmailValue, String assigneeNameValue) {
        String assigneeEmail = trimToNull(assigneeEmailValue);
        String assigneeName = trimToNull(assigneeNameValue);

        if (assigneeEmail != null) {
            ProjectParticipant emailMatch = resolveSingleMatch(
                    projectParticipantRepository.findAllByProjectIdAndUserEmail(projectId, assigneeEmail),
                    "email",
                    assigneeEmail
            );
            if (emailMatch != null) {
                return emailMatch;
            }
        }

        if (assigneeName != null) {
            ProjectParticipant nameMatch = resolveSingleMatch(
                    projectParticipantRepository.findAllByProjectIdAndUserName(projectId, assigneeName),
                    "name",
                    assigneeName
            );
            if (nameMatch != null) {
                return nameMatch;
            }
        }

        throw new IllegalArgumentException("Action item assignee could not be matched to a project participant: "
                + formatAssignee(assigneeEmail, assigneeName) + ".");
    }

    private ProjectParticipant resolveSingleMatch(
            List<ProjectParticipant> matches,
            String matchType,
            String assignee
    ) {
        if (matches.isEmpty()) {
            return null;
        }
        if (matches.size() == 1) {
            return matches.get(0);
        }

        List<ProjectParticipant> preferredMatches = preferActiveMatches(matches);
        if (preferredMatches.size() == 1) {
            return preferredMatches.get(0);
        }

        throw new IllegalArgumentException("Action item assignee is ambiguous by " + matchType
                + ": " + assignee + ".");
    }

    private List<ProjectParticipant> preferActiveMatches(List<ProjectParticipant> matches) {
        List<ProjectParticipant> preferredMatches = matches.stream()
                .filter(this::isActiveParticipant)
                .toList();
        if (preferredMatches.isEmpty()) {
            preferredMatches = matches;
        }

        List<ProjectParticipant> activeUserMatches = preferredMatches.stream()
                .filter(this::isActiveUser)
                .toList();
        if (!activeUserMatches.isEmpty()) {
            preferredMatches = activeUserMatches;
        }

        return preferredMatches;
    }

    private boolean isActiveParticipant(ProjectParticipant participant) {
        return participant.getStatus() == ProjectMemberStatus.ACTIVE;
    }

    private boolean isActiveUser(ProjectParticipant participant) {
        User user = participant.getUser();
        return user != null && Boolean.TRUE.equals(user.getStatus());
    }

    private String formatAssignee(String assigneeEmail, String assigneeName) {
        if (assigneeEmail != null && assigneeName != null) {
            return "email=" + assigneeEmail + ", name=" + assigneeName;
        }
        if (assigneeEmail != null) {
            return "email=" + assigneeEmail;
        }
        return "name=" + assigneeName;
    }

    private ActionItemPriority parsePriority(String priority) {
        try {
            return ActionItemPriority.valueOf(priority.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Action item priority must be HIGH, MEDIUM, or LOW.");
        }
    }

    private ActionItemStatus parseStatus(String status) {
        ActionItemStatus actionItemStatus = ACTION_STATUS_MAP.get(status.trim());
        if (actionItemStatus == null) {
            throw new IllegalArgumentException("Action item status must be 미착수, 진행중, or 완료.");
        }

        return actionItemStatus;
    }

    private ActionItemDto toActionItemDto(ActionItem actionItem) {
        return ActionItemDto.builder()
                .assigneeName(actionItem.getAssigneeName())
                .assigneeEmail(actionItem.getAssigneeEmail())
                .task(actionItem.getTask())
                .startDate(actionItem.getStartDate())
                .dueDate(actionItem.getDueDate())
                .priority(actionItem.getPriority() == null ? null : actionItem.getPriority().name())
                .status(toResponseStatus(actionItem.getStatus()))
                .build();
    }

    private String toResponseStatus(ActionItemStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case TODO -> "미착수";
            case IN_PROGRESS -> "진행중";
            case DONE -> "완료";
        };
    }

    private NotionFullSummaryRequestDto toNotionFullSummaryRequest(Meeting meeting, Summary summary) {
        return NotionFullSummaryRequestDto.builder()
                .meetingId(meeting.getId())
                .meetingTitle(meeting.getTitle())
                .meetingDate(meeting.getMeetingDate())
                .meetingSummary(meeting.getShortSummary())
                .objective(summary.getObjective())
                .discussion(summary.getDiscussion())
                .decision(summary.getDecision())
                .build();
    }

    private NotionActionItemRequestDto toNotionActionItemRequest(Meeting meeting, ActionItem actionItem) {
        return NotionActionItemRequestDto.builder()
                .meetingId(meeting.getId())
                .meetingTitle(meeting.getTitle())
                .actionItemId(actionItem.getId())
                .assigneeName(actionItem.getAssigneeName())
                .assigneeEmail(actionItem.getAssigneeEmail())
                .task(actionItem.getTask())
                .startDate(actionItem.getStartDate())
                .dueDate(actionItem.getDueDate())
                .priority(actionItem.getPriority() == null ? null : actionItem.getPriority().name())
                .status(toResponseStatus(actionItem.getStatus()))
                .notionPageId(actionItem.getNotionPageId())
                .build();
    }

    private NotionActionItemUploadResultDto toNotionActionItemUploadResult(ActionItemNotionUpload upload) {
        return NotionActionItemUploadResultDto.builder()
                .actionItemId(upload.actionItem().getId())
                .notionPageId(upload.result().getNotionPageId())
                .notionPageUrl(upload.result().getNotionPageUrl())
                .build();
    }

    private record ActionItemNotionUpload(ActionItem actionItem, NotionUploadResultDto result) {
    }

    private SavedFullSummaryResponseDto toSavedFullSummaryResponse(Meeting meeting, Summary summary) {
        return SavedFullSummaryResponseDto.builder()
                .meetingId(meeting.getId())
                .summary(SummaryContentDto.builder()
                        .objective(summary.getObjective())
                        .discussion(summary.getDiscussion())
                        .decision(summary.getDecision())
                        .build())
                .build();
    }

    private SavedShortSummaryResponseDto toSavedShortSummaryResponse(Meeting meeting) {
        return SavedShortSummaryResponseDto.builder()
                .meetingId(meeting.getId())
                .meetingSummary(meeting.getShortSummary())
                .build();
    }

    private SavedActionItemResponseDto toSavedActionItemResponse(ActionItem actionItem) {
        ProjectParticipant assignee = actionItem.getAssignee();

        return SavedActionItemResponseDto.builder()
                .actionItemId(actionItem.getId())
                .projectMemberId(assignee == null ? null : assignee.getId())
                .assigneeName(actionItem.getAssigneeName())
                .assigneeEmail(actionItem.getAssigneeEmail())
                .task(actionItem.getTask())
                .startDate(actionItem.getStartDate())
                .dueDate(actionItem.getDueDate())
                .priority(actionItem.getPriority() == null ? null : actionItem.getPriority().name())
                .status(toResponseStatus(actionItem.getStatus()))
                .actionItemNotionPageId(actionItem.getNotionPageId())
                .actionItemNotionPageUrl(actionItem.getNotionPageUrl())
                .build();
    }

    private Meeting getMeeting(Long meetingId) {
        if (meetingId == null) {
            throw new IllegalArgumentException("Meeting id is required.");
        }

        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found."));
    }

    private void validateFullSummaryUpdateRequest(SummaryContentDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Summary content is required.");
        }
        if (!StringUtils.hasText(request.getObjective())) {
            throw new IllegalArgumentException("Summary objective is required.");
        }
        if (!StringUtils.hasText(request.getDiscussion())) {
            throw new IllegalArgumentException("Summary discussion is required.");
        }
        if (!StringUtils.hasText(request.getDecision())) {
            throw new IllegalArgumentException("Summary decision is required.");
        }
    }

    private void validateShortSummaryUpdateRequest(ShortSummaryUpdateRequestDto request) {
        if (request == null || !StringUtils.hasText(request.getMeetingSummary())) {
            throw new IllegalArgumentException("Meeting summary is required.");
        }
    }

    private void validateActionItemsUpdateRequest(ActionItemsUpdateRequestDto request) {
        if (request == null || request.getActionItems() == null) {
            throw new IllegalArgumentException("Action items are required.");
        }
    }

    private void validateActionItemUpdateRequest(ActionItemUpdateRequestDto actionItem) {
        if (actionItem == null) {
            throw new IllegalArgumentException("Action item is required.");
        }
        if (!StringUtils.hasText(actionItem.getTask())) {
            throw new IllegalArgumentException("Action item task is required.");
        }
        if (!StringUtils.hasText(actionItem.getPriority())) {
            throw new IllegalArgumentException("Action item priority is required.");
        }
        parsePriority(actionItem.getPriority());

        if (!StringUtils.hasText(actionItem.getStatus())) {
            throw new IllegalArgumentException("Action item status is required.");
        }
        parseStatus(actionItem.getStatus());

        if (!StringUtils.hasText(actionItem.getAssigneeEmail()) && !StringUtils.hasText(actionItem.getAssigneeName())) {
            throw new IllegalArgumentException("Action item assignee name or email is required.");
        }
    }

    private void validateSaveRequest(SummarySaveRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Summary save request is required.");
        }
        SummaryContentDto summary = request.getSummary();
        if (summary == null) {
            throw new IllegalArgumentException("Summary content is required.");
        }
        if (!StringUtils.hasText(summary.getObjective())) {
            throw new IllegalArgumentException("Summary objective is required.");
        }
        if (!StringUtils.hasText(summary.getDiscussion())) {
            throw new IllegalArgumentException("Summary discussion is required.");
        }
        if (!StringUtils.hasText(summary.getDecision())) {
            throw new IllegalArgumentException("Summary decision is required.");
        }
        if (!StringUtils.hasText(request.getMeetingSummary())) {
            throw new IllegalArgumentException("Meeting summary is required.");
        }
        if (request.getActionItems() == null) {
            throw new IllegalArgumentException("Action items are required.");
        }

        request.getActionItems().forEach(this::validateActionItem);
    }

    private void validateActionItem(ActionItemDto actionItem) {
        if (actionItem == null) {
            throw new IllegalArgumentException("Action item is required.");
        }
        if (!StringUtils.hasText(actionItem.getTask())) {
            throw new IllegalArgumentException("Action item task is required.");
        }
        if (!StringUtils.hasText(actionItem.getPriority())) {
            throw new IllegalArgumentException("Action item priority is required.");
        }
        parsePriority(actionItem.getPriority());

        if (!StringUtils.hasText(actionItem.getStatus())) {
            throw new IllegalArgumentException("Action item status is required.");
        }
        parseStatus(actionItem.getStatus());

        if (!StringUtils.hasText(actionItem.getAssigneeEmail()) && !StringUtils.hasText(actionItem.getAssigneeName())) {
            throw new IllegalArgumentException("Action item assignee name or email is required.");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private List<ParticipantInfoDto> getParticipantInfos(Long projectId) {
        return projectParticipantRepository.findByProjectId(projectId)
                .stream()
                .map(this::toParticipantInfo)
                .toList();
    }

    private ParticipantInfoDto toParticipantInfo(ProjectParticipant participant) {
        User user = participant.getUser();

        return ParticipantInfoDto.builder()
                .projectMemberId(participant.getId())
                .userId(user == null ? null : user.getId())
                .userName(user == null ? null : user.getName())
                .userEmail(user == null ? null : user.getEmail())
                .projectMemberRole(participant.getRole() == null ? null : participant.getRole().name())
                .projectMemberGrade(participant.getGrade() == null ? null : participant.getGrade().name())
                .build();
    }

    private void validateResponse(AiSummaryResponseDto response) {
        if (response == null) {
            throw new IllegalStateException("AI summary response is empty.");
        }

        SummaryContentDto summary = response.getSummary();
        if (summary == null) {
            throw new IllegalStateException("AI summary content is missing.");
        }
        if (!StringUtils.hasText(summary.getObjective())) {
            throw new IllegalStateException("AI summary objective is missing.");
        }
        if (!StringUtils.hasText(summary.getDiscussion())) {
            throw new IllegalStateException("AI summary discussion is missing.");
        }
        if (!StringUtils.hasText(summary.getDecision())) {
            throw new IllegalStateException("AI summary decision is missing.");
        }
        if (!StringUtils.hasText(response.getMeetingSummary())) {
            throw new IllegalStateException("AI meeting summary is missing.");
        }
        if (response.getActionItems() == null) {
            throw new IllegalStateException("AI action items are missing.");
        }
        if (response.getActionCandidates() == null) {
            throw new IllegalStateException("AI action candidates are missing.");
        }
    }
}
