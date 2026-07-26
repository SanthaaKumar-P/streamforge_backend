package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_analysis")
public class AIAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long analysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    @JsonBackReference
    private Show show;

    @Column(name = "summary")
    private String summary;

    @Column(name = "predicted_genre")
    private String predictedGenre;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "originality_score")
    private BigDecimal originalityScore;

    @Column(name = "market_potential_score")
    private BigDecimal marketPotentialScore;

    @Column(name = "predicted_success_rate")
    private BigDecimal predictedSuccessRate;

    @Column(name = "recommendations")
    private String recommendations;

}