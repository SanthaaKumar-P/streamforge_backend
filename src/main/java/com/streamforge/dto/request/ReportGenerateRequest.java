package com.streamforge.dto.request;

import com.streamforge.enums.ReportFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportGenerateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Report name is required")
    private String reportName;

    @NotBlank(message = "Report type is required")
    private String reportType;

    @NotNull(message = "Report format is required")
    private ReportFormat format;
}