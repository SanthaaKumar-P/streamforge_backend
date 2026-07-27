package com.streamforge.repository;

import com.streamforge.entity.ProductionTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionTeamRepository extends JpaRepository<ProductionTeam, Long> {

    List<ProductionTeam> findByProductionProductionId(Long productionId);

    List<ProductionTeam> findByUserUserId(Long userId);

}