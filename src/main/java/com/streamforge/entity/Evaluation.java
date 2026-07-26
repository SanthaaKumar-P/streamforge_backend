package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.streamforge.enums.EvaluationDecision;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluations")
public class Evaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    @JsonBackReference
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    @JsonBackReference
    private User evaluator;

    @Column(name = "originality_score")
    private Integer originalityScore;

    @Column(name = "creativity_score")
    private Integer creativityScore;

    @Column(name = "market_potential_score")
    private Integer marketPotentialScore;

    @Column(name = "feasibility_score")
    private Integer feasibilityScore;

    @Column(name = "overall_score")
    private BigDecimal overallScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private EvaluationDecision decision;

    @Column(name = "remarks")
    private String remarks;

    @Builder.Default
    @OneToMany(
            mappedBy = "evaluation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<EvaluationComment> comments = new ArrayList<>();

}