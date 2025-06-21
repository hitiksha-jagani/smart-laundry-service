package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.OrderTrendGraphDTO;
import com.SmartLaundry.dto.Admin.PerUserReportResponseDTO;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.RejectedOrders;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.RejectedOrdersRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RejectedOrdersRepository rejectedOrdersRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    public OrderTrendGraphDTO getOrderTrendGraph(String filter) {
        LocalDate today = LocalDate.now();
        LocalDateTime end = today.atTime(23, 59, 59);
        LocalDateTime start;

        List<String> labels = new ArrayList<>();
        List<OrderTrendGraphDTO.DataPoint> orders = new ArrayList<>();
        List<OrderTrendGraphDTO.DataPoint> cancelled = new ArrayList<>();
        List<OrderTrendGraphDTO.DataPoint> rejected = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "yearly" :
                start = LocalDate.of(today.getYear(), 1, 1).atStartOfDay();
                for (int month = 1; month <= today.getMonthValue(); month++) {
                    LocalDateTime monthStart = LocalDate.of(today.getYear(), month, 1).atStartOfDay();
                    LocalDateTime monthEnd = monthStart.plusMonths(1).minusNanos(1);
                    String label = monthStart.getMonth().name().substring(0, 3);

                    orders.add(new OrderTrendGraphDTO.DataPoint(label,
                            orderRepository.countByCreatedAtBetween(monthStart, monthEnd)));

                    cancelled.add(new OrderTrendGraphDTO.DataPoint(label,
                            orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.CANCELLED, monthStart, monthEnd)));

                    rejected.add(new OrderTrendGraphDTO.DataPoint(label,
                            rejectedOrdersRepository.countDistinctOrdersByCreatedAtBetween(monthStart, monthEnd)));

                    labels.add(label);
                }
                break;

            case "quarterly" :
                int month = today.getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(today.getYear(), startMonth, 1).atStartOfDay();

                for (int m = startMonth; m < startMonth + 3; m++) {
                    if (m > today.getMonthValue()) break;

                    LocalDateTime monthStart = LocalDate.of(today.getYear(), m, 1).atStartOfDay();
                    LocalDateTime monthEnd = monthStart.plusMonths(1).minusNanos(1);
                    String label = monthStart.getMonth().name().substring(0, 3);

                    orders.add(new OrderTrendGraphDTO.DataPoint(label,
                            orderRepository.countByCreatedAtBetween(monthStart, monthEnd)));

                    cancelled.add(new OrderTrendGraphDTO.DataPoint(label,
                            orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.CANCELLED, monthStart, monthEnd)));

                    rejected.add(new OrderTrendGraphDTO.DataPoint(label,
                            rejectedOrdersRepository.countDistinctOrdersByCreatedAtBetween(monthStart, monthEnd)));

                    labels.add(label);
                }
                break;

            case "monthly" :
            default :
                    start = today.withDayOfMonth(1).atStartOfDay();
                    int daysInMonth = today.getDayOfMonth();

                    for (int d = 1; d <= daysInMonth; d++) {
                        LocalDateTime dayStart = LocalDate.of(today.getYear(), today.getMonth(), d).atStartOfDay();
                        LocalDateTime dayEnd = dayStart.plusDays(1).minusNanos(1);
                        String label = String.valueOf(d);

                        orders.add(new OrderTrendGraphDTO.DataPoint(label,
                                orderRepository.countByCreatedAtBetween(dayStart, dayEnd)));

                        cancelled.add(new OrderTrendGraphDTO.DataPoint(label,
                                orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.CANCELLED, dayStart, dayEnd)));

                        rejected.add(new OrderTrendGraphDTO.DataPoint(label,
                                rejectedOrdersRepository.countDistinctOrdersByCreatedAtBetween(dayStart, dayEnd)));

                        labels.add(label);
                    }
        }

        return OrderTrendGraphDTO.builder()
                .orderVolumeTrend(orders)
                .cancelledOrderTrend(cancelled)
                .rejectedOrderTrend(rejected)
                .build();
    }

    public List<PerUserReportResponseDTO> getProviderAgentOrderSummary(String role, String keyword, String sortBy, String filter) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (filter.toLowerCase()) {
            case "yearly" : start = LocalDate.of(LocalDate.now().getYear(), 1, 1).atStartOfDay();
                break;
            case "quarterly" :
                int q = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
                int startMonth = (q - 1) * 3 + 1;
                start = LocalDate.of(LocalDate.now().getYear(), startMonth, 1).atStartOfDay();
                break;
            case "monthly":
            default : start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }

        if ("service provider".equalsIgnoreCase(role)) {

            List<ServiceProvider> providers = keyword != null && !keyword.isBlank()
                    ? serviceProviderRepository.findByBusinessNameContainingIgnoreCase(keyword)
                    : serviceProviderRepository.findAll();

            return providers.stream().map(provider -> {
                long orderCount = orderRepository.countByServiceProviderAndCreatedAtBetween(provider, start, end);
                long rejectedCount = rejectedOrdersRepository.countDistinctOrdersByServiceProvider(provider.getServiceProviderId(), start, end);

                return PerUserReportResponseDTO.builder()
                        .userId(provider.getUser().getUserId())
                        .id(provider.getServiceProviderId())
                        .businessName(provider.getBusinessName())
                        .orderCount(orderCount)
                        .rejectedOrderCount(rejectedCount)
                        .build();
            }).sorted((a, b) -> {
                return "rejected".equalsIgnoreCase(sortBy)
                        ? Long.compare(b.getRejectedOrderCount(), a.getRejectedOrderCount())
                        : Long.compare(b.getOrderCount(), a.getOrderCount());
            }).collect(Collectors.toList());

        } else if ("delivery agent".equalsIgnoreCase(role)) {
            List<DeliveryAgent> agents = deliveryAgentRepository.findAll();

            return agents.stream().map(agent -> {
                long orderCount = orderRepository.countByDeliveryDeliveryAgentOrPickupAgentBetween(agent.getDeliveryAgentId(), start, end);
                long rejectedCount = rejectedOrdersRepository.countDistinctOrdersByAgent(agent.getDeliveryAgentId(), start, end);

                return PerUserReportResponseDTO.builder()
                        .userId(agent.getUsers().getUserId())
                        .id(agent.getDeliveryAgentId())
                        .orderCount(orderCount)
                        .rejectedOrderCount(rejectedCount)
                        .build();
            }).sorted((a, b) -> {
                return "rejected".equalsIgnoreCase(sortBy)
                        ? Long.compare(b.getRejectedOrderCount(), a.getRejectedOrderCount())
                        : Long.compare(b.getOrderCount(), a.getOrderCount());
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }


    public OrderTrendGraphDTO getOrderGraphForUser(String id, String filter) {
        String role = determineRoleFromId(id);
        return getProviderAgentOrderGraph(role, id, filter);
    }

    private String determineRoleFromId(String id) {
        if (id.startsWith("SP") && serviceProviderRepository.existsById(id)) {
            return "provider";
        }
        if (id.startsWith("DA") && deliveryAgentRepository.existsById(id)) {
            return "agent";
        }
        throw new IllegalArgumentException("Invalid or unknown ID format: " + id);
    }


    public OrderTrendGraphDTO getProviderAgentOrderGraph(String role, String id, String filter) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter;

        List<OrderTrendGraphDTO.DataPoint> orderTrend = new ArrayList<>();
        List<OrderTrendGraphDTO.DataPoint> cancelledTrend = new ArrayList<>();
        List<OrderTrendGraphDTO.DataPoint> rejectedTrend = new ArrayList<>();

        switch (filter.toLowerCase()) {

            case "yearly":
                // Show Jan to current month of current year
                formatter = DateTimeFormatter.ofPattern("MMM");
                int currentMonth = LocalDate.now().getMonthValue();
                for (int i = 1; i <= currentMonth; i++) {
                    LocalDateTime s = LocalDate.of(LocalDate.now().getYear(), i, 1).atStartOfDay();
                    LocalDateTime e = s.plusMonths(1);
                    String label = formatter.format(s);
                    orderTrend.add(new OrderTrendGraphDTO.DataPoint(label, countOrders(role, id, s, e)));
                    cancelledTrend.add(new OrderTrendGraphDTO.DataPoint(label, countCancelledOrders(role, id, s, e)));
                    rejectedTrend.add(new OrderTrendGraphDTO.DataPoint(label, countRejectedOrders(role, id, s, e)));
                }
                break;

            case "quarterly":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                LocalDate now = LocalDate.now();
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;

                for (int i = 3; i >= 0; i--) {
                    int quarterOffset = currentQuarter - i;
                    int year = now.getYear();
                    if (quarterOffset <= 0) {
                        quarterOffset += 4;
                        year -= 1;
                    }

                    int startMonth = (quarterOffset - 1) * 3 + 1;
                    LocalDateTime s = LocalDate.of(year, startMonth, 1).atStartOfDay();
                    LocalDateTime e = s.plusMonths(3);
                    String label = "Q" + quarterOffset + " " + year;

                    orderTrend.add(new OrderTrendGraphDTO.DataPoint(label, countOrders(role, id, s, e)));
                    cancelledTrend.add(new OrderTrendGraphDTO.DataPoint(label, countCancelledOrders(role, id, s, e)));
                    rejectedTrend.add(new OrderTrendGraphDTO.DataPoint(label, countRejectedOrders(role, id, s, e)));
                }
                break;

            case "monthly":
            default:
                formatter = DateTimeFormatter.ofPattern("dd MMM");
                LocalDate startDate = LocalDate.now().withDayOfMonth(1);
                start = startDate.atStartOfDay();
                int days = LocalDate.now().lengthOfMonth();
                for (int i = 0; i < days; i++) {
                    LocalDateTime s = start.plusDays(i);
                    LocalDateTime e = s.plusDays(1);
                    String label = formatter.format(s);
                    orderTrend.add(new OrderTrendGraphDTO.DataPoint(label, countOrders(role, id, s, e)));
                    cancelledTrend.add(new OrderTrendGraphDTO.DataPoint(label, countCancelledOrders(role, id, s, e)));
                    rejectedTrend.add(new OrderTrendGraphDTO.DataPoint(label, countRejectedOrders(role, id, s, e)));
                }
                break;
        }

        return OrderTrendGraphDTO.builder()
                .orderVolumeTrend(orderTrend)
                .cancelledOrderTrend(cancelledTrend)
                .rejectedOrderTrend(rejectedTrend)
                .build();
    }


    private long countOrders(String role, String id, LocalDateTime start, LocalDateTime end) {
        if ("provider".equalsIgnoreCase(role)) {
            return orderRepository.countByServiceProviderIdAndCreatedAtBetween(id, start, end);
        } else {
            return orderRepository.countByAgentIdAndCreatedAtBetween(id, start, end);
        }
    }

    private long countCancelledOrders(String role, String id, LocalDateTime start, LocalDateTime end) {
        if ("provider".equalsIgnoreCase(role)) {
            return orderRepository.countByServiceProviderIdAndStatusAndCreatedAtBetween(id, OrderStatus.CANCELLED, start, end);
        } else {
            return orderRepository.countByAgentIdAndStatusAndCreatedAtBetween(id, OrderStatus.CANCELLED, start, end);
        }
    }

    private long countRejectedOrders(String role, String id, LocalDateTime start, LocalDateTime end) {
        if ("provider".equalsIgnoreCase(role)) {
            return rejectedOrdersRepository.countDistinctOrdersByServiceProvider(id, start, end);
        } else {
            return rejectedOrdersRepository.countDistinctOrdersByAgent(id, start, end);
        }
    }



}
