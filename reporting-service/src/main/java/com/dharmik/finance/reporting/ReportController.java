package com.dharmik.finance.reporting;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/reports/pdf-report")
    public ResponseEntity<byte[]> getPdfReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader("Authorization") String token
    ) {
        try {
            byte[] pdfReport = reportService.generatePdfReport(startDate, endDate, token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    .body(pdfReport);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/api/reports/excel-report")
    public ResponseEntity<byte[]> getExcelReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader("Authorization") String token
    ) {
        try {
            byte[] excelReport = reportService.generateExcelReport(startDate, endDate, token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
                    .body(excelReport);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}