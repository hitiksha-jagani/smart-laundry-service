package com.SmartLaundry.dto.Admin;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerGraphOverviewDTO {
    private List<MonthlyUserTrendDTO> usersThisYearMonthlyTrend;
    private List<DailyUserTrendDTO> newUsersThisMonthDailyTrend;
    private List<YearlyUserCountDTO> userGrowthTrend;
    private List<RegionUserDistributionDTO> regionWiseDistribution;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyUserTrendDTO {
        private String month; // e.g., "Jan", "Feb"
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyUserTrendDTO {
        private int day; // day of month
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YearlyUserCountDTO {
        private int year;
        private long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegionUserDistributionDTO {
        private String city;
        private long userCount;
    }
}

