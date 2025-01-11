package com.dharmik.finance.reporting;

import com.dharmik.finance.reporting.client.TransactionClient;
import com.dharmik.finance.reporting.client.MLPredictionClient;
import com.dharmik.finance.reporting.exception.TransactionServiceException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final TransactionClient transactionClient;
    private final MLPredictionClient mlPredictionClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportService(TransactionClient transactionClient, MLPredictionClient mlPredictionClient) {
        this.transactionClient = transactionClient;
        this.mlPredictionClient = mlPredictionClient;
    }

    /**
     * Generates an Excel report of transactions.
     */
    public byte[] generateExcelReport(String startDate, String endDate, String token) throws Exception {
        List<Map<String, Object>> transactions = fetchTransactions(startDate, endDate, token);

        // Sort transactions by date in ascending order
        sortTransactionsByDate(transactions);

        // Get the predicted amount from the ML service
        double predictedAmount = getPredictedAmount(transactions);

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

        // Add predicted amount to the report
        Row predictionRow = sheet.createRow(rowNum);
        predictionRow.createCell(0).setCellValue("Predicted Spending (Next Day)");
        predictionRow.createCell(1).setCellValue(predictedAmount);

        // Add chart to Excel
        addChartToExcel(sheet, transactions);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    /**
     * Generates a PDF report of transactions.
     */
    public byte[] generatePdfReport(String startDate, String endDate, String token) throws Exception {
        List<Map<String, Object>> transactions = fetchTransactions(startDate, endDate, token);

        // Sort transactions by date in ascending order
        sortTransactionsByDate(transactions);

        // Get the predicted amount from the ML service
        double predictedAmount = getPredictedAmount(transactions);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setLeading(16f);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Transaction Report");
        contentStream.newLine();
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        for (Map<String, Object> transaction : transactions) {
            contentStream.showText("Date: " + transaction.get("date").toString());
            contentStream.newLine();
            contentStream.showText("Description: " + transaction.get("description").toString());
            contentStream.newLine();
            contentStream.showText("Amount: " + transaction.get("amount").toString() + ", Type: " + transaction.get("type").toString());
            contentStream.newLine();
            contentStream.newLine();
        }

        // Add predicted amount to the PDF report
        contentStream.showText("Predicted Spending for Next Day: $" + predictedAmount);
        contentStream.endText();
        contentStream.close();

        // Add chart to PDF
        addChartToPdf(document, transactions);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        return out.toByteArray();
    }

    /**
     * Fetches transactions from the transaction-service.
     */
    private List<Map<String, Object>> fetchTransactions(String startDate, String endDate, String token) throws Exception {
        try {
            return transactionClient.getTransactions(startDate, endDate, token);
        } catch (Exception ex) {
            System.err.println("Error while fetching transactions: " + ex.getMessage());
            throw new TransactionServiceException("Failed to fetch transactions from transaction-service.", ex);
        }
    }

    /**
     * Calls the ML service to get the predicted spending.
     */
    private double getPredictedAmount(List<Map<String, Object>> transactions) {
        Map<String, Object> request = new HashMap<>();
        request.put("transactions", transactions);

        try {
            Map<String, Double> response = mlPredictionClient.getPrediction(request, "application/json");
            return response.getOrDefault("predicted_amount", 0.0);
        } catch (Exception ex) {
            System.err.println("Error while calling ML service: " + ex.getMessage());
            return 0.0;
        }
    }


    /**
     * Sorts transactions by date in ascending order.
     */
    private void sortTransactionsByDate(List<Map<String, Object>> transactions) {
        transactions.sort(Comparator.comparing(t -> LocalDate.parse(t.get("date").toString(), DATE_FORMATTER)));
    }

    /**
     * Adds a chart to the Excel report.
     */
    private void addChartToExcel(Sheet sheet, List<Map<String, Object>> transactions) {
        DefaultCategoryDataset dataset = createDataset(transactions);

        // TODO: Use Apache POI chart APIs if required
    }

    /**
     * Adds a chart to the PDF report.
     */
    private void addChartToPdf(PDDocument document, List<Map<String, Object>> transactions) throws Exception {
        DefaultCategoryDataset dataset = createDataset(transactions);

        JFreeChart chart = ChartFactory.createLineChart(
                "Transaction Trend", "Date", "Amount",
                dataset, PlotOrientation.VERTICAL,
                true, true, false
        );

        BufferedImage chartImage = chart.createBufferedImage(500, 400);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertImageToByteArray(chartImage), "chart");

        PDPage chartPage = new PDPage();
        document.addPage(chartPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, chartPage);
        contentStream.drawImage(pdImage, 50, 200, 500, 400);  // Adjusted size and position
        contentStream.close();
    }

    /**
     * Creates a dataset from transactions.
     */
    private DefaultCategoryDataset createDataset(List<Map<String, Object>> transactions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map<String, Object> transaction : transactions) {
            String date = transaction.get("date").toString();
            double amount = Double.parseDouble(transaction.get("amount").toString());
            dataset.addValue(amount, "Amount", date);
        }
        return dataset;
    }

    /**
     * Converts a BufferedImage to a byte array.
     */
    private byte[] convertImageToByteArray(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}