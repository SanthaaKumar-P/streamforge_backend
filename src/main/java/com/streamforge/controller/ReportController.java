package com.streamforge.controller;

import com.streamforge.dto.response.ReportResponse;
import com.streamforge.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER','PRODUCER')")
    public ResponseEntity<ReportResponse> generateReport(
            @RequestParam Long userId,
            @RequestParam String reportName,
            @RequestParam String reportType) {

        return ResponseEntity.ok(
                reportService.generateReport(
                        userId,
                        reportName,
                        reportType
                )
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReportResponse>> getReportsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                reportService.getReportsByUser(userId)
        );
    }

}