package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(origins = "http://localhost:63342")
public class ticketController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JWTService jwtService;

    private String extractUserIdFromRequest(HttpServletRequest request) {
        return (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
    }

    @PostMapping("/raise")
    public ResponseEntity<String> raiseTicket(
            HttpServletRequest request,
            @RequestPart("ticket") String ticketJson,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile) {

        try {
            String userId = extractUserIdFromRequest(request);

            // Manually parse the ticket JSON
            ObjectMapper mapper = new ObjectMapper();
            RaiseTicketRequestDto dto = mapper.readValue(ticketJson, RaiseTicketRequestDto.class);

            orderService.raiseTicket(userId, dto, photoFile);
            return ResponseEntity.ok("Ticket raised successfully.");
        } catch (IOException e) {
            e.printStackTrace(); // log the exact reason
            return ResponseEntity.internalServerError().body("Failed to save ticket photo: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
