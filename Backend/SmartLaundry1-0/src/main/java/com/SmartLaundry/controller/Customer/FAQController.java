package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.Customer.FAQRequestDto;
import com.SmartLaundry.dto.Customer.FAQResponseDto;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Customer.FAQService;

import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FAQController {

    @Autowired
    private final FAQService faqService;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final UserRepository usersRepository;
    private String extractUserIdFromRequest(HttpServletRequest request) {
        return (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
    }
    // Utility method to extract user ID from JWT and validate user block status
    private void checkIfUserBlocked(HttpServletRequest request) throws AccessDeniedException {
        String userId = extractUserIdFromRequest(request); // You must already have this method
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }
    }

    @PostMapping
    public ResponseEntity<FAQResponseDto> createFAQ(HttpServletRequest request, @RequestBody FAQRequestDto dto)
            throws AccessDeniedException {
        checkIfUserBlocked(request);
        FAQResponseDto created = faqService.createFAQ(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/visible")
    public ResponseEntity<List<FAQResponseDto>> getAllVisibleFAQs(HttpServletRequest request)
            throws AccessDeniedException {
        checkIfUserBlocked(request);
        return ResponseEntity.ok(faqService.getAllVisibleFAQs());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<FAQResponseDto>> getFAQsByTicket(HttpServletRequest request, @PathVariable Long ticketId)
            throws AccessDeniedException {
        checkIfUserBlocked(request);
        return ResponseEntity.ok(faqService.getFAQsByTicket(ticketId));
    }

    @DeleteMapping("/{faqId}")
    public ResponseEntity<Void> deleteFAQ(HttpServletRequest request, @PathVariable Long faqId)
            throws AccessDeniedException {
        checkIfUserBlocked(request);
        faqService.deleteFAQ(faqId);
        return ResponseEntity.noContent().build();
    }
}
