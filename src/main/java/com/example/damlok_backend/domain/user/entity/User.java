package com.example.damlok_backend.domain.user.entity;

import com.example.damlok_backend.domain.company.entity.Company;
import com.example.damlok_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_profile_image")
    private String profileImage;

    @Column(name = "user_last_login")
    private LocalDateTime lastLogin;

    @Column(name = "user_phone")
    private String phone;

    @Column(name = "user_department", nullable = false)
    private String department;

    @Column(name = "user_role", nullable = false)
    private String role;

    @Builder.Default
    @Column(name = "user_status", nullable = false)
    private Boolean status = true;
}
