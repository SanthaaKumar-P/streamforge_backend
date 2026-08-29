package com.streamforge.service;

import com.streamforge.dto.response.AuditLogResponse;

import java.util.List;

public interface AuditLogService {

    AuditLogResponse createLog(
            Long userId,
            String action,
            String entityName,
            Long entityId
    );

    List<AuditLogResponse> getUserLogs(
            Long userId
    );
}