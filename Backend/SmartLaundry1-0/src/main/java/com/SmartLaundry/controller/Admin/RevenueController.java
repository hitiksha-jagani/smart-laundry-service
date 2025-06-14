package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO;
import com.SmartLaundry.dto.Admin.RevenueBreakdownResponseTableDTO;
import com.SmartLaundry.dto.Admin.RevenueResponseDTO;
import com.SmartLaundry.dto.Admin.TotalRevenueDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RevenueService;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/revenue/")
public class RevenueController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RevenueService revenueService;

    @Autowired
    private RoleCheckingService roleCheckingService;

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
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getSummary(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/breakdown/table
    // Return revue breakdown in the form of table.
    @GetMapping("breakdown/table")
    public ResponseEntity<RevenueBreakdownResponseTableDTO> getRevenueBreakdownTable(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getBreakdownTable(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/breakdown/graph
    // Return revue breakdown in the form of graph.
    @GetMapping("breakdown/graph")
    public ResponseEntity<List<RevenueBreakDownResponseGraphDTO>> getRevenueBreakdownGraph(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getBreakdownGraph(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/total-revenue
    // Return order details with revenue.
    @GetMapping("/total-revenue")
    public ResponseEntity<List<TotalRevenueDTO>> getTotalRevenue(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getTotalRevenue(user, filter, startDate, endDate));
    }

}
