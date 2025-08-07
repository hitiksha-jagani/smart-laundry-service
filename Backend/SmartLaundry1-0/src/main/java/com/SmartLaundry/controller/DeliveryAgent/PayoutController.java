package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.FeedbackResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PayoutResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PayoutSummaryResponseDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.DeliveryAgent.PayoutService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payouts")
public class PayoutController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PayoutService payoutService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/payouts/summary
    // Return count of  Total earnings, pending payouts
    @GetMapping("/summary")
    public ResponseEntity<PayoutSummaryResponseDTO> getFPayoutSummary(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(payoutService.getSummary(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/payouts/all
    // Return list of  Total earnings
    @GetMapping("/all")
    public ResponseEntity<List<PayoutResponseDTO>> getAllPayouts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(payoutService.getPayouts(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/payouts/paid
    // Return list of  Total earnings
    @GetMapping("/paid")
    public ResponseEntity<List<PayoutResponseDTO>> getPaidPayouts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(payoutService.getPaidPayouts(user, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/payouts/pending
    // Return list of pending payouts based on selected filter
    @GetMapping("/pending")
    public ResponseEntity<List<PayoutResponseDTO>> getPendingPayouts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException{
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(payoutService.getPendingPayouts(user, filter, startDate, endDate));
    }

}
