package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.streamforge.enums.ProductionStatus;
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
@Table(name = "productions")
public class Production extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "production_id")
    private Long productionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    @JsonBackReference
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id", nullable = false)
    @JsonBackReference
    private User producer;

    @Enumerated(EnumType.STRING)
    @Column(name = "production_status")
    private ProductionStatus productionStatus;

    @Column(name = "allocated_budget")
    private BigDecimal allocatedBudget;

    @Column(name = "actual_budget")
    private BigDecimal actualBudget;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "notes")
    private String notes;

    @Builder.Default
    @OneToMany(
            mappedBy = "production",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<ProductionTeam> teamMembers = new ArrayList<>();

}