package com.streamforge.service;

import com.streamforge.dto.request.ReportGenerateRequest;
import com.streamforge.dto.response.ReportResponse;

import java.util.List;

public interface ReportService {

    ReportResponse generateReport(
            ReportGenerateRequest request
    );

    List<ReportResponse> getReportsByUser(
            Long userId
    );

}