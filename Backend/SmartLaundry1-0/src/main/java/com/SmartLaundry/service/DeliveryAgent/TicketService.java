package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.TicketRepository;
import com.SmartLaundry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DeliveryAgentProfileService deliveryAgentProfileService ;

    public String raiseTicket(String userId, RaiseTicketRequestDto raiseTicketRequestDto, MultipartFile image) throws IOException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"DELIVERY_AGENT".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        String uploadDir = "/media/hitiksha/C/DAIICT/Summer internship/images/tickets/delivery_agent/" + user.getUserId();
        String photo = deliveryAgentProfileService.saveFile(image, uploadDir, userId);

        Ticket ticket = Ticket.builder()
                .title(raiseTicketRequestDto.getTitle())
                .description(raiseTicketRequestDto.getDescription())
                .photo(raiseTicketRequestDto.getPhoto() != null ? photo : null)
                .category(raiseTicketRequestDto.getCategory())
                .response(null)
                .respondedAt(null)
                .user(user)
                .status(String.valueOf(TicketStatus.NOT_RESPONDED))
                .build();

        ticketRepository.save(ticket);

        return "Ticket raised successfully.";
    }

    // Return list of raised tickets
    public List<RaiseTicketRequestDto> getAllTickets(String title, String category, TicketStatus status, String userID) throws AccessDeniedException {

        Users user = userRepository.findById(userID)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"DELIVERY_AGENT".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        List<Ticket> tickets = ticketRepository.findByTitleOrCategoryOrStatus(title, category, status);

        return tickets.stream().map(this::mapToTicketDTO).collect(Collectors.toList());

    }

    private RaiseTicketRequestDto mapToTicketDTO(Ticket ticket){

        RaiseTicketRequestDto dto = RaiseTicketRequestDto.builder()
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo("/ticket/image/" + ticket.getUser().getUserId())
                    .category(ticket.getCategory())
                    .submittedAt(ticket.getSubmittedAt())
                    .status(ticket.getStatus())
                    .response(ticket.getResponse())
                    .respondedAt(ticket.getRespondedAt())
                    .build();

        return dto;
    }
}
