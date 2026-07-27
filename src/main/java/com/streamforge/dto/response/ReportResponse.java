package com.streamforge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long reportId;

    private String reportName;

    private String reportType;

    private String filePath;

}