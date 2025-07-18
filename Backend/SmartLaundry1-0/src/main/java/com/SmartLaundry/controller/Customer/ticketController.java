package com.SmartLaundry.controller.Customer;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

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
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(origins = "http://localhost:63342")
public class ticketController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepository usersRepository;

    private String extractUserIdFromRequest(HttpServletRequest request) {
        return (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
    }

    @PostMapping("/raise")
    public ResponseEntity<String> raiseTicket(
            HttpServletRequest request,
            @RequestPart("ticket") String ticketJson,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile) throws AccessDeniedException {

        try {
            String userId = extractUserIdFromRequest(request);
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.isBlocked()) {
                throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
            }
            // Manually parse the ticket JSON with proper module
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
