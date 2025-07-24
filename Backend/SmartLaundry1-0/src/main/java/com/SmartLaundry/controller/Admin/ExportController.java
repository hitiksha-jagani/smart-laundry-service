package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ExportRequestDTO;
import com.SmartLaundry.service.Admin.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping
    public ResponseEntity<byte[]> exportReport(@RequestBody ExportRequestDTO request) {
        byte[] fileContent = exportService.export(request);
        LocalDate today = LocalDate.now();

        String fileName = today + "_" + request.getPageType() + "_report." + request.getExportType().toLowerCase();
        String contentType = request.getExportType().equalsIgnoreCase("pdf") ?
                "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileContent);
    }

}
