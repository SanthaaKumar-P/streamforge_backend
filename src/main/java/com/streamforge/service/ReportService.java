package com.streamforge.service;

import com.streamforge.dto.response.ReportResponse;

import java.util.List;

public interface ReportService {

    ReportResponse generateReport(
            Long userId,
            String reportName,
            String reportType
    );

    List<ReportResponse> getReportsByUser(Long userId);

}