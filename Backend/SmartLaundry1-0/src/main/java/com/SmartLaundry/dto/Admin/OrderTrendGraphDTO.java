package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTrendGraphDTO {
    private List<DataPoint> orderVolumeTrend;
    private List<DataPoint> cancelledOrderTrend;
    private List<DataPoint> rejectedOrderTrend;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DataPoint {
        private String label; // e.g. "Jan", "Q1", "2024", or "05-Jun"
        private long count;
    }
}

