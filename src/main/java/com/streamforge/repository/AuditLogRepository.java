package com.streamforge.repository;

import com.streamforge.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserUserId(Long userId);

    List<AuditLog> findByEntityName(String entityName);

}