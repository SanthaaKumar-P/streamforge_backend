package com.streamforge.controller;

import com.streamforge.entity.Report;
import com.streamforge.entity.User;
import com.streamforge.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports/files")
@RequiredArgsConstructor
public class ReportFileController {

    /*
     * =========================================================
     * REPORT DIRECTORY
     * =========================================================
     *
     * ReportFileServiceImpl generates files inside:
     *
     * uploads/reports
     *
     * So the controller reads from the same directory.
     */
    private static final Path REPORT_DIRECTORY =
            Paths.get(
                    "uploads",
                    "reports"
            )
            .toAbsolutePath()
            .normalize();


    private final ReportRepository reportRepository;


    /*
     * =========================================================
     * DOWNLOAD REPORT
     * =========================================================
     *
     * Example:
     *
     * GET
     * /api/reports/files/
     * Content_Pipeline_Report_20260909_214500.pdf
     *
     */
    @GetMapping("/{fileName:.+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable String fileName
    ) {

        try {

            /*
             * =================================================
             * 1. BASIC VALIDATION
             * =================================================
             */

            if (
                    fileName == null ||
                    fileName.isBlank()
            ) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }


            /*
             * =================================================
             * 2. SECURITY
             * =================================================
             *
             * Only a filename is accepted.
             *
             * Examples that must NOT be allowed:
             *
             * ../../application.properties
             * ../something.txt
             *
             */

            Path file =
                    REPORT_DIRECTORY
                            .resolve(fileName)
                            .normalize();


            /*
             * Make sure resolved file stays inside
             * uploads/reports.
             */
            if (
                    !file.startsWith(
                            REPORT_DIRECTORY
                    )
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.BAD_REQUEST
                        )
                        .build();
            }


            /*
             * =================================================
             * 3. CHECK FILE EXISTS
             * =================================================
             */

            if (
                    !Files.exists(file) ||
                    !Files.isRegularFile(file)
            ) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            /*
             * =================================================
             * 4. FIND REPORT RECORD
             * =================================================
             *
             * ReportFileServiceImpl stores something similar to:
             *
             * uploads/reports/
             * Content_Pipeline_Report_20260909_214500.pdf
             *
             * We compare only the filename portion.
             */
            String requestedFileName =
                    file.getFileName()
                            .toString();


            Optional<Report> report =
                    reportRepository
                            .findAll()
                            .stream()
                            .filter(
                                    currentReport -> {

                                        String storedPath =
                                                currentReport
                                                        .getFilePath();

                                        if (
                                                storedPath == null ||
                                                storedPath.isBlank()
                                        ) {

                                            return false;
                                        }


                                        String normalizedPath =
                                                storedPath
                                                        .replace(
                                                                "\\",
                                                                "/"
                                                        );


                                        return normalizedPath
                                                .endsWith(
                                                        "/"
                                                                + requestedFileName
                                                )
                                                ||
                                                normalizedPath.equals(
                                                        requestedFileName
                                                );
                                    }
                            )
                            .findFirst();


            /*
             * File must belong to a report record.
             */
            if (report.isEmpty()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            Report storedReport =
                    report.get();


            /*
             * =================================================
             * 5. AUTHENTICATED USER
             * =================================================
             */

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();


            if (
                    authentication == null ||
                    !authentication.isAuthenticated()
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .build();
            }


            String loggedInUsername =
                    authentication
                            .getName();


            /*
             * =================================================
             * 6. ADMIN / OWNER CHECK
             * =================================================
             *
             * Admin:
             *     can download every report.
             *
             * Report owner:
             *     can download their own report.
             */

            boolean isAdmin =
                    authentication
                            .getAuthorities()
                            .stream()
                            .anyMatch(
                                    authority -> {

                                        String name =
                                                authority
                                                        .getAuthority();

                                        return
                                                "ROLE_ADMIN"
                                                        .equalsIgnoreCase(
                                                                name
                                                        )
                                                        ||
                                                "ADMIN"
                                                        .equalsIgnoreCase(
                                                                name
                                                        );
                                    }
                            );


            /*
             * Admin can download directly.
             */
            if (!isAdmin) {

                User generatedBy =
                        storedReport
                                .getGeneratedBy();


                /*
                 * Report must have an owner.
                 */
                if (
                        generatedBy == null ||
                        generatedBy.getUsername() == null
                ) {

                    return ResponseEntity
                            .status(
                                    HttpStatus.FORBIDDEN
                            )
                            .build();
                }


                /*
                 * Compare logged-in username
                 * with report owner.
                 */
                boolean isOwner =
                        generatedBy
                                .getUsername()
                                .equalsIgnoreCase(
                                        loggedInUsername
                                );


                if (!isOwner) {

                    return ResponseEntity
                            .status(
                                    HttpStatus.FORBIDDEN
                            )
                            .build();
                }
            }


            /*
             * =================================================
             * 7. CREATE RESOURCE
             * =================================================
             */

            Resource resource =
                    new FileSystemResource(
                            file
                    );


            if (
                    !resource.exists() ||
                    !resource.isReadable()
            ) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            /*
             * =================================================
             * 8. DETERMINE CONTENT TYPE
             * =================================================
             */

            String contentType =
                    Files.probeContentType(
                            file
                    );


            if (contentType == null) {

                String lowerFileName =
                        requestedFileName
                                .toLowerCase(
                                        Locale.ROOT
                                );


                if (
                        lowerFileName
                                .endsWith(".pdf")
                ) {

                    contentType =
                            "application/pdf";

                } else if (
                        lowerFileName
                                .endsWith(".xlsx")
                ) {

                    contentType =
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

                } else if (
                        lowerFileName
                                .endsWith(".csv")
                ) {

                    contentType =
                            "text/csv";

                } else {

                    contentType =
                            MediaType
                                    .APPLICATION_OCTET_STREAM_VALUE;
                }
            }


            MediaType mediaType;


            try {

                mediaType =
                        MediaType.parseMediaType(
                                contentType
                        );

            } catch (Exception ignored) {

                mediaType =
                        MediaType
                                .APPLICATION_OCTET_STREAM;
            }


            /*
             * =================================================
             * 9. DOWNLOAD RESPONSE
             * =================================================
             */

            return ResponseEntity
                    .ok()
                    .contentType(
                            mediaType
                    )
                    .contentLength(
                            Files.size(file)
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition
                                    .attachment()
                                    .filename(
                                            requestedFileName
                                    )
                                    .build()
                                    .toString()
                    )
                    .body(
                            resource
                    );


        } catch (Exception e) {

            /*
             * Development logging.
             */
            e.printStackTrace();


            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .build();
        }
    }
}