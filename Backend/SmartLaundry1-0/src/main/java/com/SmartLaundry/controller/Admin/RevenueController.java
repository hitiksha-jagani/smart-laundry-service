package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.*;
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
    @GetMapping("/breakdown/table")
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
    @GetMapping("/breakdown/graph")
    public ResponseEntity<RevenueBreakDownResponseGraphDTO> getRevenueBreakdownGraph(
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

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/total-revenue/{orderId}}
    // Return order details with revenue.
    @GetMapping("/total-revenue/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderDetail(
            HttpServletRequest request,
           @PathVariable String orderId ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getOrderDetail(orderId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/trends
    // Return graph for revenue trends for admin, gross sales, total payouts, delivery agent payouts or service provider payouts
    // based on filter monthly, quarterly, yearly
    @GetMapping("/trends")
    public ResponseEntity<RevenueTrendDTO> getRevenueTrendsGraph(HttpServletRequest request,
                                                                       @RequestParam(defaultValue = "gross_sales") String type,
                                                                 @RequestParam(defaultValue = "monthly") String filter) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getRevenueTrendsGraph(type, filter));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/insights
    // Return best performing service provider, most active delivery agent and highest value order
//    @GetMapping("/insights")
//    public ResponseEntity<InsightResponseDTO> getInsights(HttpServletRequest request,
//                                                          @RequestParam(defaultValue = "monthly") String filter) throws AccessDeniedException {
//        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
//        Users user = roleCheckingService.checkUser(userId);
//        roleCheckingService.isAdmin(user);
//        return ResponseEntity.ok(revenueService.getInsights(filter));
//    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/provider-analytics-list
    // Return revenue analytics list of service providers
    @GetMapping("/provider-analytics-list")
    public ResponseEntity<List<ServiceProviderRevenueTableDTO>> getServiceProviderRevenueTable(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getProviderRevenueTable(filter));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/provider-analytics-list/{providerId}
    // Return revenue analytics graph for each service provider
    @GetMapping("/provider-analytics-list/{providerId}")
    public ResponseEntity<ServiceProviderRevenueGraphDTO> getServiceProviderRevenueGraph(
            HttpServletRequest request,
            @PathVariable String providerId,
            @RequestParam(defaultValue = "Overall") String filter) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getProviderRevenueGraph(filter, providerId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/agent-analytics-list
    // Return revenue analytics list of delivery agents
    @GetMapping("/agent-analytics-list")
    public ResponseEntity<List<DeliveryAgentRevenueTableDTO>> getDeliveryAgentRevenueTable(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getAgentRevenueTable(filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/revenue/agent-analytics-list/{agentId}
    // Return revenue analytics graph for each delivery agent
    @GetMapping("/agent-analytics-list/{agentId}")
    public ResponseEntity<DeliveryAgentRevenueGraphDTO> getDeliveryAgentRevenueGraph(
            HttpServletRequest request,
            @PathVariable String agentId,
            @RequestParam(defaultValue = "Overall") String filter) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(revenueService.getAgentRevenueGraph(filter, agentId));
    }

}
