package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.RevenueResponseDTO;
import com.SmartLaundry.service.Admin.RevenueService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RevenueService revenueService;

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/set
    // Set revenue
    @PutMapping("/set")
    public ResponseEntity<String> setRevenue(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(revenueService.setRevenue(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/summary
    // Return count of Total revenue, total orders, gross sales, service providers payouts, delivery agents payouts, average order value.
    @GetMapping("/summary")
    public ResponseEntity<RevenueResponseDTO> getRevenueSummary(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(revenueService.getSummary(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/breakdown
    // Return count of Total revenue, total orders, gross sales, service providers payouts, delivery agents payouts, average order value.
    @GetMapping("/breakdown")
    public ResponseEntity<RevenueResponseDTO> getRevenueBreakdown(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(revenueService.getBreakdown(userId, filter, startDate, endDate));
    }

}
