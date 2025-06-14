package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.FeedbackResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.FeedbackSummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PayoutResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PayoutSummaryResponseDTO;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.FeedbackAgents;
import com.SmartLaundry.model.Payout;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.PayoutRepository;
import com.SmartLaundry.repository.UserRepository;
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

// @author Hitiksha Jagani
@Service
public class PayoutService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private PayoutRepository payoutRepository;

    // Return summary count of payouts
    public PayoutSummaryResponseDTO getSummary(Users user, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not found."));

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        Double totalEarnings;
        Double pendingPayouts;

        String id = user.getUserId();
        System.out.println("id : " + id);

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                totalEarnings = payoutRepository.findTotalEarningsByUserIdAndDateRange(id, start, end);
                pendingPayouts = payoutRepository.findPendingPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                totalEarnings = payoutRepository.findTotalEarningsByUserIdAndDateRange(id, start, end);
                pendingPayouts = payoutRepository.findPendingPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                totalEarnings = payoutRepository.findTotalEarningsByUserIdAndDateRange(id, start, end);
                pendingPayouts = payoutRepository.findPendingPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                totalEarnings = payoutRepository.findTotalEarningsByUserIdAndDateRange(id, start, end);
                pendingPayouts = payoutRepository.findPendingPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "overall":
            default:
                totalEarnings = payoutRepository.findTotalEarningsByUserId(user.getUserId());
                pendingPayouts = payoutRepository.findPendingPayoutsByUserId(user.getUserId());
        }

        PayoutSummaryResponseDTO payoutSummaryResponseDTO = PayoutSummaryResponseDTO.builder()
                .totalEarnings(totalEarnings)
                .pendingPayouts(pendingPayouts)
                .build();

        return payoutSummaryResponseDTO;

    }

    // Return list of payouts
    public List<PayoutResponseDTO> getPayouts(Users user, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        String id = user.getUserId();

        List<Payout> payouts = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                payouts = payoutRepository.findPayoutsByUserIdAndDateRange(id, start, end);
                break;
            case "overall":
            default:
                payouts = payoutRepository.findAll();
        }

        return payouts.stream().map(this::mapToPayoutResponseDTO).collect(Collectors.toList());
    }

    private PayoutResponseDTO mapToPayoutResponseDTO(Payout payout){

        PayoutResponseDTO dto = PayoutResponseDTO.builder()
                .payoutId(payout.getPayoutId())
                .orderId(payout.getPayment().getOrder().getOrderId())
                .deliveryEarning(payout.getDeliveryEarning())
                .charge(payout.getCharge())
                .payoutStatus(payout.getStatus())
                .finalAmount(payout.getFinalAmount())
                .dateTime(payout.getDateTime())
                .build();

        return dto;
    }

    public List<PayoutResponseDTO> getPendingPayouts(Users user, String filter, LocalDate startDate, LocalDate endDate) {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        String id = user.getUserId();

        List<Payout> payouts = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRangeAndStatus(id, start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRangeAndStatus(id, start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                payouts = payoutRepository.findPayoutsByUserIdAndDateRangeAndStatus(id, start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                payouts = payoutRepository.findPayoutsByUserIdAndDateRangeAndStatus(id, start, end);
                break;
            case "overall":
            default:
                payouts = payoutRepository.findAll();
        }

        return payouts.stream().map(this::mapToPayoutResponseDTO).collect(Collectors.toList());
    }
}

