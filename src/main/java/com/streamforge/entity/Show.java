package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.streamforge.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "shows")
public class Show extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_id")
    private Long showId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonBackReference
    private User creator;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "synopsis")
    private String synopsis;

    @Column(name = "language")
    private String language;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "estimated_budget")
    private BigDecimal estimatedBudget;

    @Column(name = "expected_release_date")
    private LocalDate expectedReleaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShowStatus status;

    @Builder.Default
    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Episode> episodes = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<ShowFile> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Evaluation> evaluations = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Production> productions = new ArrayList<>();

    @OneToOne(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonManagedReference
    private AIAnalysis aiAnalysis;

    @Builder.Default
    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<ShowGenre> showGenres = new ArrayList<>();
}