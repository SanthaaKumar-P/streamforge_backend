package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    private Role role;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "bio")
    private String bio;

    @Column(name = "is_active")
    private Boolean isActive;

    // Sessions

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Session> sessions = new ArrayList<>();

    // Shows

    @Builder.Default
    @OneToMany(
            mappedBy = "creator",
            fetch = FetchType.LAZY
    )
    private List<Show> shows = new ArrayList<>();

    // Evaluations

    @Builder.Default
    @OneToMany(
            mappedBy = "evaluator",
            fetch = FetchType.LAZY
    )
    private List<Evaluation> evaluations = new ArrayList<>();

    // Productions

    @Builder.Default
    @OneToMany(
            mappedBy = "producer",
            fetch = FetchType.LAZY
    )
    private List<Production> productions = new ArrayList<>();

    // Notifications

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<Notification> notifications = new ArrayList<>();

    // Reports

    @Builder.Default
    @OneToMany(
            mappedBy = "generatedBy",
            fetch = FetchType.LAZY
    )
    private List<Report> reports = new ArrayList<>();

    // Audit Logs

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<AuditLog> auditLogs = new ArrayList<>();

    // Uploaded Files

    @Builder.Default
    @OneToMany(
            mappedBy = "uploadedBy",
            fetch = FetchType.LAZY
    )
    private List<ShowFile> uploadedFiles = new ArrayList<>();

    // Evaluation Comments

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<EvaluationComment> comments = new ArrayList<>();

    // Production Team

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<ProductionTeam> productionTeams = new ArrayList<>();

}