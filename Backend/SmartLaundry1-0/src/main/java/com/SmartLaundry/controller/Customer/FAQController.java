package com.SmartLaundry.controller.Customer;
import com.SmartLaundry.dto.Customer.FAQRequestDto;
import com.SmartLaundry.dto.Customer.FAQResponseDto;
import com.SmartLaundry.service.Customer.FAQService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;

    // Create a new FAQ linked to a ticket
    @PostMapping
    public ResponseEntity<FAQResponseDto> createFAQ(@RequestBody FAQRequestDto dto) {
        FAQResponseDto created = faqService.createFAQ(dto);
        return ResponseEntity.ok(created);
    }

    // Get all visible FAQs (across all tickets)
    @GetMapping("/visible")
    public ResponseEntity<List<FAQResponseDto>> getAllVisibleFAQs() {
        return ResponseEntity.ok(faqService.getAllVisibleFAQs());
    }

    // Get all FAQs for a specific ticket
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<FAQResponseDto>> getFAQsByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(faqService.getFAQsByTicket(ticketId));
    }

    // Delete FAQ by id
    @DeleteMapping("/{faqId}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long faqId) {
        faqService.deleteFAQ(faqId);
        return ResponseEntity.noContent().build();
    }
}

