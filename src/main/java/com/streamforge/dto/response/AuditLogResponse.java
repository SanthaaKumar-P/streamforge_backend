package com.streamforge.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long logId;

    private String action;

    private String entityName;

    private Long entityId;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime actionTime;
}