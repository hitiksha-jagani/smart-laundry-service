package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.dto.DeliveryAgent.FeedbackResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.FeedbackSummaryResponseDTO;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.FeedbackAgents;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.FeedbackAgentsRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Admin.RevenueService;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackAgentsRepository feedbackAgentsRepository;

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private RevenueService revenueService;

    // Return summary count of feedback
    public FeedbackSummaryResponseDTO getSummary(Users user, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        DeliveryAgent deliveryAgent = roleCheckingService.checkDeliveryAgent(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        Long totalReviews;
        Double avgRating;

        String agentId = deliveryAgent.getDeliveryAgentId();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                totalReviews = feedbackAgentsRepository.findReviewCountByAgentIdAndDateRange(start, end, agentId);
                avgRating = feedbackAgentsRepository.findAverageRatingByAgentIdAndDateRange(agentId, start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                totalReviews = feedbackAgentsRepository.findReviewCountByAgentIdAndDateRange(start, end, agentId);
                avgRating = feedbackAgentsRepository.findAverageRatingByAgentIdAndDateRange(agentId, start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                totalReviews = feedbackAgentsRepository.findReviewCountByAgentIdAndDateRange(start, end, agentId);
                avgRating = feedbackAgentsRepository.findAverageRatingByAgentIdAndDateRange(agentId, start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                totalReviews = feedbackAgentsRepository.findReviewCountByAgentIdAndDateRange(start, end, agentId);
                avgRating = feedbackAgentsRepository.findAverageRatingByAgentIdAndDateRange(agentId, start, end);
                break;
            case "overall":
            default:
                totalReviews = feedbackAgentsRepository.findReviewCountByAgentId(agentId);
                avgRating = feedbackAgentsRepository.findAverageRatingByAgentId(agentId);
        }

        FeedbackSummaryResponseDTO feedbackSummaryResponseDTO = FeedbackSummaryResponseDTO.builder()
                .totalReviews(totalReviews)
                .averageRating(revenueService.round(avgRating, 2))
                .build();

        return feedbackSummaryResponseDTO;

    }

    public List<FeedbackResponseDTO> getFeedbacks(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<FeedbackAgents> feedbackAgents = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                feedbackAgents = feedbackAgentsRepository.findByDateAndId(userId, start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                feedbackAgents = feedbackAgentsRepository.findByDateAndId(userId, start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                feedbackAgents = feedbackAgentsRepository.findByDateAndId(userId, start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                feedbackAgents = feedbackAgentsRepository.findByDateAndId(userId, start, end);
                break;
            case "overall":
            default:
                feedbackAgents = feedbackAgentsRepository.findById(userId);
        }

        return feedbackAgents.stream().map(this::mapToFeedbackResponseDTO).collect(Collectors.toList());
    }

    private FeedbackResponseDTO mapToFeedbackResponseDTO(FeedbackAgents feedbackAgents){

        FeedbackResponseDTO dto = FeedbackResponseDTO.builder()
                .orderId(feedbackAgents.getOrder().getOrderId())
                .customerName(feedbackAgents.getUser().getFirstName() + feedbackAgents.getUser().getLastName())
                .rating(feedbackAgents.getRating())
                .review(feedbackAgents.getReview())
                .createdAt(feedbackAgents.getCreatedAt())
                .build();

        return dto;
    }
}
