package com.streamforge.service;

import com.streamforge.enums.ReportFormat;

import java.io.IOException;

public interface ReportFileService {

    String generate(
            String reportName,
            String reportType,
            Long userId,
            ReportFormat format
    ) throws IOException;

}