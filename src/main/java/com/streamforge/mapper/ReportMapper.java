package com.streamforge.mapper;

import com.streamforge.dto.response.ReportResponse;
import com.streamforge.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report report){

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reportName(report.getReportName())
                .reportType(report.getReportType())
                .filePath(report.getFilePath())
                .build();

    }

}