package com.SmartLaundry.service.Customer;
import com.SmartLaundry.dto.Customer.FAQRequestDto;
import com.SmartLaundry.dto.Customer.FAQResponseDto;
import com.SmartLaundry.model.FAQ;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.repository.FAQRepository;
import com.SmartLaundry.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;
    private final TicketRepository ticketRepository;

    // 1. Create FAQ (with or without a ticket)
    public FAQResponseDto createFAQ(FAQRequestDto dto) {
        Ticket ticket = null;
        if (dto.getTicketId() != null) {
            ticket = ticketRepository.findById(dto.getTicketId())
                    .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + dto.getTicketId()));
        }

        FAQ faq = FAQ.builder()
                .ticket(ticket)
                .visibilityStatus(dto.getVisibilityStatus())
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .category(dto.getCategory())
                .build();

        return mapToDto(faqRepository.save(faq));
    }

    // 2. Auto-create FAQ when responding to a ticket
    public void respondToTicket(Long ticketId, String responseText, boolean makeFaqVisible) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setResponse(responseText);
        ticket.setStatus("RESPONDED");
        ticket.setRespondedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        FAQ faq = FAQ.builder()
                .ticket(ticket)
                .visibilityStatus(makeFaqVisible)
                .question(ticket.getTitle())
                .answer(responseText)
                .category(ticket.getCategory() != null ? ticket.getCategory() : "General")
                .build();

        faqRepository.save(faq);
    }

    // 3. Get all visible FAQs
    public List<FAQResponseDto> getAllVisibleFAQs() {
        return faqRepository.findByVisibilityStatusTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 4. Get FAQs by ticket
    public List<FAQResponseDto> getFAQsByTicket(Long ticketId) {
        return faqRepository.findByTicket_TicketId(ticketId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 5. Delete FAQ
    public void deleteFAQ(Long faqId) {
        faqRepository.deleteById(faqId);
    }

    // Helper
    private FAQResponseDto mapToDto(FAQ faq) {
        FAQResponseDto dto = new FAQResponseDto();
        dto.setFaqId(faq.getFaqId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setVisibilityStatus(faq.getVisibilityStatus());
        dto.setCategory(faq.getCategory());
        if (faq.getTicket() != null) {
            dto.setTicketId(faq.getTicket().getTicketId());
        }
        return dto;
    }
}

