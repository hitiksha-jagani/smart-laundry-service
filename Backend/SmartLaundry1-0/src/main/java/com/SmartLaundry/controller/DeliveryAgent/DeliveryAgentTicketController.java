package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.DeliveryAgent.TicketService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

// @author Hitiksha Jagani
@RestController
@RequestMapping("/agent-ticket")
public class DeliveryAgentTicketController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    ObjectMapper objectMapper;

    // http://localhost:8080/agent-ticket/raise
    // Render form to submit ticket
    @PostMapping("/raise")
    public ResponseEntity<String> raiseTicket(@RequestPart("data") @Valid String  data,
                                              @RequestPart(value = "photo", required = false) MultipartFile photo,
                                              HttpServletRequest request) throws IOException {
        // Parse JSON string to DTO
        ObjectMapper mapper = new ObjectMapper();
        RaiseTicketRequestDto dataJson = mapper.readValue(data, RaiseTicketRequestDto.class);
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }
        return ResponseEntity.ok(ticketService.raiseTicket(user, dataJson, photo));
    }

    // http://localhost:8080/agent-ticket/list
    // Return list of tickets based on search
    @GetMapping("/list")
    public ResponseEntity<List<RaiseTicketRequestDto>> getAllTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TicketStatus status,
            HttpServletRequest request
    ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        List<RaiseTicketRequestDto> serviceProviders = ticketService.getAllTickets(category, status, user);
        return ResponseEntity.ok(serviceProviders);
    }

}
