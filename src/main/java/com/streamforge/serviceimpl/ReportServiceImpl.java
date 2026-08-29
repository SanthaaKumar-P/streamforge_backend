package com.streamforge.serviceimpl;

import com.streamforge.dto.request.ReportGenerateRequest;
import com.streamforge.dto.response.ReportResponse;
import com.streamforge.entity.Report;
import com.streamforge.entity.User;
import com.streamforge.enums.ReportFormat;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ReportMapper;
import com.streamforge.repository.ReportRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ReportFileService;
import com.streamforge.service.ReportService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl
        implements ReportService {


    private final ReportRepository reportRepository;

    private final UserRepository userRepository;

    private final ReportMapper reportMapper;

    private final ReportFileService reportFileService;


    // =========================================================
    // GENERATE REPORT
    // =========================================================

    @Override
    public ReportResponse generateReport(
            ReportGenerateRequest request
    ) {


        // ---------------------------------------------
        // Find user
        // ---------------------------------------------

        User user =
                userRepository
                        .findById(
                                request.getUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + request.getUserId()
                                )
                        );


        // ---------------------------------------------
        // Validate format
        // ---------------------------------------------

        ReportFormat format =
                request.getFormat();


        if (format == null) {

            throw new IllegalArgumentException(
                    "Report format is required"
            );
        }


        // ---------------------------------------------
        // Generate physical file
        // ---------------------------------------------

        String filePath;


        try {


            String generatedPath =
                    reportFileService.generate(
                            request.getReportName(),
                            request.getReportType(),
                            request.getUserId(),
                            format
                    );


            /*
             * Example generatedPath:
             *
             * uploads/reports/
             * Content_Pipeline_Report_20260829_163000.pdf
             */


            String fileName =
                    Paths.get(
                            generatedPath
                    )
                    .getFileName()
                    .toString();


            /*
             * Store URL path in database.
             *
             * Example:
             *
             * /reports/
             * Content_Pipeline_Report_20260829_163000.pdf
             */

            filePath =
                    "/reports/"
                            + fileName;


        } catch (IOException e) {


            throw new RuntimeException(
                    "Failed to generate report file",
                    e
            );
        }


        // ---------------------------------------------
        // Save report record
        // ---------------------------------------------

        Report report =
                Report.builder()
                        .generatedBy(user)
                        .reportName(
                                request.getReportName()
                        )
                        .reportType(
                                request.getReportType()
                        )
                        .filePath(
                                filePath
                        )
                        .build();


        Report saved =
                reportRepository.save(
                        report
                );


        // ---------------------------------------------
        // Response
        // ---------------------------------------------

        ReportResponse response =
                reportMapper.toResponse(
                        saved
                );


        response.setFormat(
                format.name()
        );


        return response;
    }


    // =========================================================
    // GET REPORTS BY USER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByUser(
            Long userId
    ) {


        if (!userRepository.existsById(
                userId
        )) {

            throw new ResourceNotFoundException(
                    "User not found with id: "
                            + userId
            );
        }


        return reportRepository
                .findByGeneratedByUserId(
                        userId
                )
                .stream()
                .map(report -> {


                    ReportResponse response =
                            reportMapper.toResponse(
                                    report
                            );


                    // Determine format from extension
                    String filePath =
                            report.getFilePath();


                    if (filePath != null) {


                        String lowerPath =
                                filePath.toLowerCase();


                        if (lowerPath.endsWith(
                                ".pdf"
                        )) {

                            response.setFormat(
                                    "PDF"
                            );


                        } else if (
                                lowerPath.endsWith(
                                        ".csv"
                                )
                        ) {

                            response.setFormat(
                                    "CSV"
                            );


                        } else if (
                                lowerPath.endsWith(
                                        ".xlsx"
                                )
                        ) {

                            response.setFormat(
                                    "XLSX"
                            );
                        }
                    }


                    return response;

                })
                .toList();
    }
}