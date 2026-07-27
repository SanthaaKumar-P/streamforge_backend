package com.streamforge.mapper;

import com.streamforge.dto.response.AuditLogResponse;
import com.streamforge.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {


    public AuditLogResponse toResponse(AuditLog auditLog){

        return AuditLogResponse.builder()
                .logId(auditLog.getLogId())
                .action(auditLog.getAction())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .actionTime(auditLog.getActionTime())
                .build();

    }

}