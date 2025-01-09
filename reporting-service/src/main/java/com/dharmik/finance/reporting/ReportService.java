package com.dharmik.finance.reporting;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final RestTemplate restTemplate;

    @Value("${transaction-service.url}")
    private String transactionServiceUrl;

    public ReportService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] generateExcelReport() throws Exception {
        // Fetch transactions from transaction-service
        List<Map<String, Object>> transactions = restTemplate.getForObject(transactionServiceUrl, List.class);

        // Generate Excel report
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Description");
        headerRow.createCell(1).setCellValue("Amount");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Type");

        int rowNum = 1;
        for (Map<String, Object> transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.get("description").toString());
            row.createCell(1).setCellValue(Double.parseDouble(transaction.get("amount").toString()));
            row.createCell(2).setCellValue(transaction.get("date").toString());
            row.createCell(3).setCellValue(transaction.get("type").toString());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}