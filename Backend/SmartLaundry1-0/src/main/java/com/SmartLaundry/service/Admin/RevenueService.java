package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.RevenueResponseDTO;
import com.SmartLaundry.dto.Admin.RevenueSettingRequestDTO;
import com.SmartLaundry.dto.Admin.RevenueSettingResponseDTO;
import com.SmartLaundry.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RevenueService {

    @Autowired
    private RoleCheckingService roleCheckingService;

    public RevenueResponseDTO getSummary(String userId, String filter, LocalDate startDate, LocalDate endDate) {
        RevenueResponseDTO dto = null;
        return dto;
    }

    public RevenueResponseDTO getBreakdown(String userId, String filter, LocalDate startDate, LocalDate endDate) {
        RevenueResponseDTO dto = null;
        return dto;
    }

    public RevenueSettingResponseDTO setRevenue(String userId, RevenueSettingRequestDTO revenueSettingRequestDTO) {

        Users user = roleCheckingService.checkUser(userId);
        RevenueSettingResponseDTO dto = null;

        return dto;
    }
}
