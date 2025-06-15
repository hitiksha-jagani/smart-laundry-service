package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO;
import com.SmartLaundry.dto.Admin.RevenueBreakdownResponseTableDTO;
import com.SmartLaundry.dto.Admin.RevenueResponseDTO;
import com.SmartLaundry.dto.Admin.TotalRevenueDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.twilio.twiml.voice.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueService {

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private RevenueRepository revenueRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PayoutRepository payoutRepository;

    public RevenueResponseDTO getSummary(Users user, String filter, LocalDate startDate, LocalDate endDate) {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        Double totalRevenue, grossSales, providerPayouts, agentPayouts, avgOrderValue;
        Long totalOrders;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER.name());
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT.name());
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER.name());
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT.name());
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER.name());
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT.name());
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER.name());
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT.name());
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "overall":
            default:
                totalRevenue = revenueRepository.findTotalRevenue();
                totalOrders = revenueRepository.findTotalOrder();
                grossSales = revenueRepository.findGrossSales();
                providerPayouts = revenueRepository.findPayoutsByRole(UserRole.SERVICE_PROVIDER.name());
                agentPayouts = revenueRepository.findPayoutsByRole(UserRole.DELIVERY_AGENT.name());
                avgOrderValue = revenueRepository.findAverageOrderValue();
        }

        RevenueResponseDTO revenueResponseDTO = RevenueResponseDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .grossSales(grossSales)
                .serviceProviderPayouts(providerPayouts)
                .deliveryAgentPayouts(agentPayouts)
                .avgOrderValue(avgOrderValue)
                .build();

        return revenueResponseDTO;
    }

    public RevenueBreakdownResponseTableDTO getBreakdownTable(Users user, String filter, LocalDate startDate, LocalDate endDate) {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        Double providerRevenue;
        Double agentRevenue;
        Double adminRevenue;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                providerRevenue = revenueRepository.findRevenueFromServiceProviderByDateRange(start, end);
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgentDateRange(start, end);
                adminRevenue = providerRevenue + agentRevenue;
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                providerRevenue = revenueRepository.findRevenueFromServiceProviderByDateRange(start, end);
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgentDateRange(start, end);
                adminRevenue = providerRevenue + agentRevenue;
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                providerRevenue = revenueRepository.findRevenueFromServiceProviderByDateRange(start, end);
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgentDateRange(start, end);
                adminRevenue = providerRevenue + agentRevenue;
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                providerRevenue = revenueRepository.findRevenueFromServiceProviderByDateRange(start, end);
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgentDateRange(start, end);
                adminRevenue = providerRevenue + agentRevenue;
                break;
            case "overall":
            default:
                providerRevenue = revenueRepository.findRevenueFromServiceProvider();
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgent();
                adminRevenue = providerRevenue + agentRevenue;
        }

        RevenueBreakdownResponseTableDTO revenueBreakdownResponseTableDTO = RevenueBreakdownResponseTableDTO.builder()
                .serviceProviderRevenue(providerRevenue)
                .deliveryAgentRevenue(agentRevenue)
                .adminRevenue(adminRevenue)
                .build();

        return revenueBreakdownResponseTableDTO;
    }

    public List<RevenueBreakDownResponseGraphDTO> getBreakdownGraph(Users user, String filter, LocalDate startDate, LocalDate endDate) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = now;
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = now;
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = now;
                break;
            case "custom":
                if (startDate == null || endDate == null)
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                break;
            case "overall":
            default:
                start = LocalDate.now().minusDays(30).atStartOfDay();
                end = now;
                break;
        }

        return revenueRepository.getRevenueBreakdownGroupedByDate(start, end);
    }

    public List<TotalRevenueDTO> getTotalRevenue(Users user, String filter, LocalDate startDate, LocalDate endDate) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        String orderId;
        List<Order> orders;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                orders = revenueRepository.findByIdAndDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                orders = revenueRepository.findByIdAndDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                orders = revenueRepository.findByIdAndDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                orders = revenueRepository.findByIdAndDateRange(start, end);
                break;
            case "overall":
            default:
                orders = revenueRepository.findById();
        }

        List<TotalRevenueDTO> revenueDTOS = new ArrayList<>();

        for(Order order : orders){
            Bill bill = billRepository.findByOrder(order);

            Payment payment = paymentRepository.findByBill(bill);
            List<Payout> payout = payoutRepository.findByPayment(payment);
            AdminRevenue adminRevenue = revenueRepository.findByPayment(payment);

            Double providerPayout = 0.0, agentPayout = 0.0;

            for(Payout pay : payout){
                if(pay.getUsers().getRole().equals(UserRole.SERVICE_PROVIDER)){
                    providerPayout = pay.getFinalAmount();
                } else {
                    agentPayout = pay.getFinalAmount();
                }
            }

            TotalRevenueDTO totalRevenueDTO = TotalRevenueDTO.builder()
                    .orderId(order.getOrderId())
                    .date(payment.getDateTime())
                    .customerName(order.getUsers().getFirstName() + order.getUsers().getLastName())
                    .totalPaid(bill.getFinalPrice())
                    .providerPayout(providerPayout)
                    .agentPayout(agentPayout)
                    .adminRevenue(adminRevenue.getTotalRevenue())
                    .build();

            revenueDTOS.add(totalRevenueDTO);
        }
     return revenueDTOS;
    }
}
