package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.Admin.ServiceProviderResponseDTO;
import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.service.DeliveryAgent.TicketService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

// @author Hitiksha Jagani
@RestController
@RequestMapping("")
public class TicketController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    ObjectMapper objectMapper;

    // http://localhost:8080/raise-ticket
    // Render form to submit ticket
    @PostMapping("/raise-ticket")
    public ResponseEntity<String> raiseTicket(@RequestPart("data") @Valid String  data,
                                              @RequestPart(value = "photo", required = false) MultipartFile photo,
                                              HttpServletRequest request) throws IOException {
        // Parse JSON string to DTO
        ObjectMapper mapper = new ObjectMapper();
        RaiseTicketRequestDto dataJson = mapper.readValue(data, RaiseTicketRequestDto.class);
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(ticketService.raiseTicket(userId, dataJson, photo));
    }

    // http://localhost:8080/tickets
    // Return list of tickets based on search
    @GetMapping("/tickets")
    public ResponseEntity<List<RaiseTicketRequestDto>> getAllTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TicketStatus status,
            HttpServletRequest request
    ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        List<RaiseTicketRequestDto> serviceProviders = ticketService.getAllTickets(category, status, userId);
        return ResponseEntity.ok(serviceProviders);
    }

}
