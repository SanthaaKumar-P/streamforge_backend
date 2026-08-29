package com.streamforge.controller;

import com.streamforge.dto.response.AuditLogResponse;
import com.streamforge.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    // =========================================================
    // CREATE AUDIT LOG
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponse> createLog(
            @RequestParam Long userId,
            @RequestParam String action,
            @RequestParam String entityName,
            @RequestParam Long entityId) {

        return ResponseEntity.ok(
                auditLogService.createLog(
                        userId,
                        action,
                        entityName,
                        entityId
                )
        );
    }

    // =========================================================
    // GET LOGS BY USER
    // =========================================================

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getUserLogs(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                auditLogService.getUserLogs(userId)
        );
    }
}