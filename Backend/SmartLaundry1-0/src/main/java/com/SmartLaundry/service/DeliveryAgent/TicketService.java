package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.TicketRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
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
    private RoleCheckingService roleCheckingService;

    @Autowired
    private DeliveryAgentProfileService deliveryAgentProfileService ;
    @Value("${DELIVERY_AGENT_TICKET}")
    private String path;
    public String raiseTicket(String userId, RaiseTicketRequestDto raiseTicketRequestDto, MultipartFile image) throws IOException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isDeliveryAgent(user);

        String uploadDir = "path" + user.getUserId();
        String photo = deliveryAgentProfileService.saveFile(image, uploadDir, userId);

        Ticket ticket = Ticket.builder()
                .title(raiseTicketRequestDto.getTitle())
                .description(raiseTicketRequestDto.getDescription())
                .photo(photo)
                .category(raiseTicketRequestDto.getCategory())
                .response(null)
                .respondedAt(null)
                .user(user)
                .status(TicketStatus.NOT_RESPONDED)
                .build();

        ticketRepository.save(ticket);

        return "Ticket raised successfully.";
    }

    // Return list of raised tickets
    public List<RaiseTicketRequestDto> getAllTickets(String category, TicketStatus status, String userID) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userID);

        roleCheckingService.isDeliveryAgent(user);

        List<Ticket> tickets;

        if(category != null && !category.isBlank()){
            tickets = ticketRepository.findTicketsByCategoryAndUser(category, userID);
        } else if(status != null) {
            tickets = ticketRepository.findTicketByStatusAndUser(status, userID);
        } else {
            tickets = ticketRepository.findByUser(user);
        }

        return tickets.stream().map(this::mapToTicketDTO).collect(Collectors.toList());

    }

    private RaiseTicketRequestDto mapToTicketDTO(Ticket ticket){

        RaiseTicketRequestDto dto = RaiseTicketRequestDto.builder()
                    .ticketId(ticket.getTicketId())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                            ? ("/complaints/image/" + ticket.getTicketId())
                            : null)
                    .category(ticket.getCategory())
                    .submittedAt(ticket.getSubmittedAt())
                    .status(ticket.getStatus())
                    .response(ticket.getResponse())
                    .respondedAt(ticket.getRespondedAt())
                    .build();

        return dto;
    }
}
