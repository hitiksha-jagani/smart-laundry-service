package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ExportRequestDTO;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class CommonExportServiceImpl implements ExportService{

    @Override
    public byte[] export(ExportRequestDTO request) {
        switch (request.getExportType().toLowerCase()) {
            case "pdf":
                return generatePDF(request);
            case "excel":
                return generateExcel(request);
            default:
                throw new IllegalArgumentException("Unsupported export type: " + request.getExportType());
        }
    }

    private byte[] generateExcel(ExportRequestDTO request) {
        // Use Apache POI to dynamically populate sheets based on request.getPageType()
        // e.g., fill revenue summary, breakdown table, etc.
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String pageType = request.getPageType().toLowerCase();
            Map<String, Object> data = request.getData();

            // Create Summary Sheet if present
            if (data.containsKey("summary")) {
                Sheet summarySheet = workbook.createSheet("Summary");
                createKeyValueSheet(summarySheet, (Map<String, Object>) data.get("summary"));
            }

            // Create Table Sheet if present
            if (data.containsKey("table")) {
                Sheet tableSheet = workbook.createSheet("Table");
                createTableSheet(tableSheet, (List<Map<String, Object>>) data.get("table"));
            }

            // Create Chart Data Sheet if present
            if (data.containsKey("chart")) {
                Sheet chartSheet = workbook.createSheet("Chart Data");
                createChartDataSheet(chartSheet, (List<Map<String, Object>>) data.get("chart"));
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    private void createKeyValueSheet(Sheet sheet, Map<String, Object> summary) {
        int rowNum = 0;
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue() != null ? entry.getValue().toString() : "");
        }
    }

    private void createTableSheet(Sheet sheet, List<Map<String, Object>> tableData) {
        if (tableData.isEmpty()) return;

        // Header
        Row headerRow = sheet.createRow(0);
        List<String> headers = tableData.get(0).keySet().stream().toList();
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        // Rows
        for (int i = 0; i < tableData.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> rowData = tableData.get(i);
            int col = 0;
            for (String key : headers) {
                row.createCell(col++).setCellValue(rowData.get(key) != null ? rowData.get(key).toString() : "");
            }
        }
    }

    private void createChartDataSheet(Sheet sheet, List<Map<String, Object>> chartData) {
        if (chartData.isEmpty()) return;

        // Assuming each chart point has "label" and "revenue"
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Label");
        header.createCell(1).setCellValue("Revenue");

        for (int i = 0; i < chartData.size(); i++) {
            Map<String, Object> point = chartData.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(point.getOrDefault("label", "").toString());
            row.createCell(1).setCellValue(Double.parseDouble(point.getOrDefault("revenue", "0").toString()));
        }
    }

    private byte[] generatePDF(ExportRequestDTO request) {
        // Use iText or JasperReports to render PDF
        // Include graphs, summary, tables, etc.
        // Charts can be passed as base64 images in the data map
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(request.getTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Summary (if present)
            if (request.getData().containsKey("summary")) {
                document.add(new Paragraph("Summary", new Font(Font.HELVETICA, 14, Font.BOLD)));
                @SuppressWarnings("unchecked")
                Map<String, Object> summary = (Map<String, Object>) request.getData().get("summary");
                for (Map.Entry<String, Object> entry : summary.entrySet()) {
                    document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
                }
                document.add(Chunk.NEWLINE);
            }

            // Table (if present)
            if (request.getData().containsKey("table")) {
                document.add(new Paragraph("Details", new Font(Font.HELVETICA, 14, Font.BOLD)));
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tableData = (List<Map<String, Object>>) request.getData().get("table");
                if (!tableData.isEmpty()) {
                    // Extract headers from first row
                    Map<String, Object> firstRow = tableData.get(0);
                    PdfPTable table = new PdfPTable(firstRow.size());
                    table.setWidthPercentage(100);

                    // Header
                    for (String key : firstRow.keySet()) {
                        PdfPCell header = new PdfPCell(new Phrase(key));
                        header.setBackgroundColor(java.awt.Color.DARK_GRAY);
                        table.addCell(header);
                    }

                    // Data rows
                    for (Map<String, Object> row : tableData) {
                        for (Object value : row.values()) {
                            table.addCell(value != null ? value.toString() : "");
                        }
                    }

                    document.add(table);
                    document.add(Chunk.NEWLINE);
                }
            }

            // Chart Placeholder (optional - OpenPDF does not support chart rendering directly)
            if ("revenue".equalsIgnoreCase(request.getPageType()) && request.getData().containsKey("graph")) {
                document.add(new Paragraph("Revenue Graph (placeholder)", new Font(Font.HELVETICA, 14, Font.BOLD)));
                document.add(new Paragraph("Graph rendering not supported in OpenPDF directly."));
                // If using image chart (like JFreeChart), you can add: document.add(Image.getInstance(...));
                document.add(Chunk.NEWLINE);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

}
