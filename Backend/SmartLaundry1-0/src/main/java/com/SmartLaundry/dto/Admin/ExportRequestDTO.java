package com.SmartLaundry.dto.Admin;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportRequestDTO {
    private String title;
    private String exportType; // "pdf" or "excel"
    private String pageType;   // "revenue", "orders", etc.
    private Map<String, Object> data; // Actual data like summaries, tables, and chart values
}

