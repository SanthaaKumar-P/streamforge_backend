package com.streamforge.repository;

import com.streamforge.entity.Production;
import com.streamforge.enums.ProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

    List<Production> findByShowShowId(Long showId);

    List<Production> findByProducerUserId(Long producerId);

    List<Production> findByProductionStatus(ProductionStatus status);

}