package com.example.damlok_backend.domain.company.entity;

import com.example.damlok_backend.global.entity.BaseEntity;
import com.example.damlok_backend.domain.user.entity.*;
import com.example.damlok_backend.domain.project.entity.*;
import com.example.damlok_backend.domain.notion.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String domain;

    private String phone;

    @Column(name = "company_notion_url")
    private String companyNotionUrl;

    @OneToMany(mappedBy = "company")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    private List<Notion> notions = new ArrayList<>();
}