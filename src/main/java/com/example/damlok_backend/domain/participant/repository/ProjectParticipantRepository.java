package com.example.damlok_backend.domain.participant.repository;

import com.example.damlok_backend.domain.participant.entity.ProjectParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectParticipantRepository extends JpaRepository<ProjectParticipant, Long>, JpaSpecificationExecutor<ProjectParticipant> {
    List<ProjectParticipant> findByProjectId(Long projectId);

    Optional<ProjectParticipant> findByProjectIdAndUserId(Long projectId, Long userId);

    @Query("""
            select participant
            from ProjectParticipant participant
            join participant.project project
            join participant.user user
            where project.id = :projectId
              and lower(user.email) = lower(:email)
            """)
    List<ProjectParticipant> findAllByProjectIdAndUserEmail(
            @Param("projectId") Long projectId,
            @Param("email") String email
    );

    @Query("""
            select participant
            from ProjectParticipant participant
            join participant.project project
            join participant.user user
            where project.id = :projectId
              and user.name = :name
            """)
    List<ProjectParticipant> findAllByProjectIdAndUserName(
            @Param("projectId") Long projectId,
            @Param("name") String name
    );

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
