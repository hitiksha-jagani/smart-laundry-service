package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.OrderTrendGraphDTO;
import com.SmartLaundry.dto.Admin.PerUserReportResponseDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.ReportService;
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
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private ReportService reportService;

    // @author Hitiksha Jagani
    // http://localhost:8080/reports/order/summary
    // Return summary card for total order, rejected orders
//    @GetMapping("/order/summary")
//    public Reponse

    // @author Hitiksha Jagani
    // http://localhost:8080/reports/order/trend
    // Return graph for order volume trend and cancelled order volume trend and rejected volume trend based on filter
    @GetMapping("/order/trend")
    public ResponseEntity<OrderTrendGraphDTO> getOrderTrendGraph(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(reportService.getOrderTrendGraph(filter));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/reports/order/user-report-list
    // Return list of service provider/delivery agent order analytics based on filter
    @GetMapping("/order/user-report-list")
    public ResponseEntity<List<PerUserReportResponseDTO>> getProviderAgentSummary(
            HttpServletRequest request,
            @RequestParam String role, // "provider" or "agent"
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "order") String sortBy,
            @RequestParam(defaultValue = "monthly") String filter
    ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(reportService.getProviderAgentOrderSummary(role, keyword, sortBy, filter));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/reports/order/user-report-list/graph/{userId}
    // Return graph of service provider/delivery agent order analytics based on filter
    @GetMapping("/order/user-report-list/graph/{userId}")
    public ResponseEntity<OrderTrendGraphDTO> getProviderAgentOrderTrendGraph(
            HttpServletRequest request,
            @PathVariable String userId,
            @RequestParam(defaultValue = "monthly") String filter
    ) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(reportService.getOrderGraphForUser(userId, filter));
    }


    // @author Hitiksha Jagani
    // http://localhost:8080/reports/review
    //

}
