package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.streamforge.enums.EpisodeStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "episodes")
public class Episode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private Long episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    @JsonBackReference
    private Show show;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EpisodeStatus status;

}