package com.streamforge.serviceimpl;

import com.streamforge.enums.ReportFormat;
import com.streamforge.service.ReportFileService;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportFileServiceImpl
        implements ReportFileService {


    private static final String REPORT_DIRECTORY =
            "uploads/reports";


    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );


    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss"
            );


    // =========================================================
    // GENERATE REPORT FILE
    // =========================================================

    @Override
    public String generate(
            String reportName,
            String reportType,
            Long userId,
            ReportFormat format
    ) throws IOException {


        if (reportName == null ||
                reportName.isBlank()) {

            throw new IllegalArgumentException(
                    "Report name is required"
            );
        }


        if (reportType == null ||
                reportType.isBlank()) {

            throw new IllegalArgumentException(
                    "Report type is required"
            );
        }


        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }


        if (format == null) {

            throw new IllegalArgumentException(
                    "Report format is required"
            );
        }


        // Create uploads/reports directory
        Path directory =
                Paths.get(
                        REPORT_DIRECTORY
                );


        Files.createDirectories(
                directory
        );


        // Make report name safe for Windows/Linux
        String safeName =
                reportName
                        .trim()
                        .replaceAll(
                                "[^a-zA-Z0-9-_]",
                                "_"
                        );


        String timestamp =
                LocalDateTime.now()
                        .format(
                                FILE_DATE_FORMAT
                        );


        String fileName =
                safeName
                        + "_"
                        + timestamp;


        Path filePath;


        // =====================================================
        // PDF
        // =====================================================

        if (format == ReportFormat.PDF) {

            filePath =
                    directory.resolve(
                            fileName + ".pdf"
                    );


            generatePdf(
                    filePath,
                    reportName,
                    reportType,
                    userId
            );
        }


        // =====================================================
        // CSV
        // =====================================================

        else if (format == ReportFormat.CSV) {

            filePath =
                    directory.resolve(
                            fileName + ".csv"
                    );


            generateCsv(
                    filePath,
                    reportName,
                    reportType,
                    userId
            );
        }


        // =====================================================
        // XLSX
        // =====================================================

        else if (format == ReportFormat.XLSX) {

            filePath =
                    directory.resolve(
                            fileName + ".xlsx"
                    );


            generateXlsx(
                    filePath,
                    reportName,
                    reportType,
                    userId
            );
        }


        else {

            throw new IllegalArgumentException(
                    "Unsupported report format: "
                            + format
            );
        }


        // Return normalized path
        return filePath
                .toString()
                .replace(
                        "\\",
                        "/"
                );
    }


    // =========================================================
    // PDF GENERATION
    // =========================================================

    private void generatePdf(
            Path path,
            String reportName,
            String reportType,
            Long userId
    ) throws IOException {


        try (
                PDDocument document =
                        new PDDocument()
        ) {


            PDPage page =
                    new PDPage(
                            PDRectangle.A4
                    );


            document.addPage(
                    page
            );


            // =================================================
            // PDF CONTENT
            // =================================================

            try (
                    PDPageContentStream content =
                            new PDPageContentStream(
                                    document,
                                    page
                            )
            ) {


                // ---------------------------------------------
                // Title
                // ---------------------------------------------

                content.beginText();


                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        20
                );


                content.newLineAtOffset(
                        50,
                        750
                );


                content.showText(
                        sanitizePdfText(
                                reportName
                        )
                );


                content.endText();


                // ---------------------------------------------
                // Details
                // ---------------------------------------------

                content.beginText();


                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        12
                );


                content.newLineAtOffset(
                        50,
                        710
                );


                content.showText(
                        "StreamForge Studio Report"
                );


                content.newLineAtOffset(
                        0,
                        -30
                );


                content.showText(
                        "Report Type: "
                                + sanitizePdfText(
                                reportType
                        )
                );


                content.newLineAtOffset(
                        0,
                        -25
                );


                content.showText(
                        "Generated By User ID: "
                                + userId
                );


                content.newLineAtOffset(
                        0,
                        -25
                );


                content.showText(
                        "Generated At: "
                                + LocalDateTime.now()
                                .format(
                                        DATE_FORMAT
                                )
                );


                content.newLineAtOffset(
                        0,
                        -50
                );


                content.showText(
                        "This report was generated"
                                + " from the StreamForge"
                                + " reporting system."
                );


                content.endText();
            }


            // Save PDF
            document.save(
                    path.toFile()
            );
        }
    }


    // =========================================================
    // CSV GENERATION
    // =========================================================

    private void generateCsv(
            Path path,
            String reportName,
            String reportType,
            Long userId
    ) throws IOException {


        try (
                BufferedWriter writer =
                        Files.newBufferedWriter(
                                path,
                                StandardCharsets.UTF_8
                        )
        ) {


            writer.write(
                    "Field,Value"
            );


            writer.newLine();


            writer.write(
                    "Report Name,"
                            + escapeCsv(
                            reportName
                    )
            );


            writer.newLine();


            writer.write(
                    "Report Type,"
                            + escapeCsv(
                            reportType
                    )
            );


            writer.newLine();


            writer.write(
                    "Generated By User ID,"
                            + userId
            );


            writer.newLine();


            writer.write(
                    "Generated At,"
                            + escapeCsv(
                            LocalDateTime.now()
                                    .format(
                                            DATE_FORMAT
                                    )
                    )
            );


            writer.newLine();
        }
    }


    // =========================================================
    // XLSX GENERATION
    // =========================================================

    private void generateXlsx(
            Path path,
            String reportName,
            String reportType,
            Long userId
    ) throws IOException {


        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {


            Sheet sheet =
                    workbook.createSheet(
                            "Report"
                    );


            // ---------------------------------------------
            // Title
            // ---------------------------------------------

            Row title =
                    sheet.createRow(
                            0
                    );


            title.createCell(0)
                    .setCellValue(
                            reportName
                    );


            // ---------------------------------------------
            // Header
            // ---------------------------------------------

            Row header =
                    sheet.createRow(
                            2
                    );


            header.createCell(0)
                    .setCellValue(
                            "Field"
                    );


            header.createCell(1)
                    .setCellValue(
                            "Value"
                    );


            // ---------------------------------------------
            // Report Type
            // ---------------------------------------------

            Row row1 =
                    sheet.createRow(
                            3
                    );


            row1.createCell(0)
                    .setCellValue(
                            "Report Type"
                    );


            row1.createCell(1)
                    .setCellValue(
                            reportType
                    );


            // ---------------------------------------------
            // User ID
            // ---------------------------------------------

            Row row2 =
                    sheet.createRow(
                            4
                    );


            row2.createCell(0)
                    .setCellValue(
                            "Generated By User ID"
                    );


            row2.createCell(1)
                    .setCellValue(
                            userId
                    );


            // ---------------------------------------------
            // Generated At
            // ---------------------------------------------

            Row row3 =
                    sheet.createRow(
                            5
                    );


            row3.createCell(0)
                    .setCellValue(
                            "Generated At"
                    );


            row3.createCell(1)
                    .setCellValue(
                            LocalDateTime.now()
                                    .format(
                                            DATE_FORMAT
                                    )
                    );


            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);


            // ---------------------------------------------
            // Write XLSX
            // ---------------------------------------------

            try (
                    OutputStream outputStream =
                            Files.newOutputStream(
                                    path
                            )
            ) {

                workbook.write(
                        outputStream
                );
            }
        }
    }


    // =========================================================
    // CSV ESCAPE
    // =========================================================

    private String escapeCsv(
            String value
    ) {


        if (value == null) {

            return "";
        }


        return "\""
                + value.replace(
                        "\"",
                        "\"\""
                )
                + "\"";
    }


    // =========================================================
    // PDF TEXT SANITIZER
    // =========================================================

    private String sanitizePdfText(
            String value
    ) {


        if (value == null) {

            return "";
        }


        // Standard Helvetica only supports
        // basic WinAnsi characters.
        return value
                .replace(
                        "\n",
                        " "
                )
                .replace(
                        "\r",
                        " "
                );
    }
}