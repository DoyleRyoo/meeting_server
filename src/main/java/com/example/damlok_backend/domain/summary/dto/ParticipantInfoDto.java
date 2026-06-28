package com.example.damlok_backend.domain.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantInfoDto {

    private Long projectMemberId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String projectMemberRole;
    private String projectMemberGrade;
}
