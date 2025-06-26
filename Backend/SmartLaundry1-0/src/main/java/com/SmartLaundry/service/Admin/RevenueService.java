package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private BookingItemRepository bookingItemRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private AdminRevenueRepository adminRevenueRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private OrderRepository orderRepository;

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
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
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
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                totalRevenue = revenueRepository.findTotalRevenueByDateRange(start, end);
                totalOrders = revenueRepository.findTotalOrderByDateRange(start, end);
                grossSales = revenueRepository.findGrossSalesByDateRange(start, end);
                providerPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                agentPayouts = revenueRepository.findPayoutsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                avgOrderValue = revenueRepository.findAverageOrderValueByDateRange(start, end);
        }

        RevenueResponseDTO revenueResponseDTO = RevenueResponseDTO.builder()
                .totalRevenue(round(totalRevenue, 2))
                .totalOrders(totalOrders)
                .grossSales(round(grossSales, 2))
                .serviceProviderPayouts(round(providerPayouts, 2))
                .deliveryAgentPayouts(round(agentPayouts, 2))
                .avgOrderValue(round(avgOrderValue, 2))
                .build();

        return revenueResponseDTO;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                providerRevenue = revenueRepository.findRevenueFromServiceProviderByDateRange(start, end);
                agentRevenue = revenueRepository.findRevenueFromDeliveryAgentDateRange(start, end);
                adminRevenue = providerRevenue + agentRevenue;
        }

        RevenueBreakdownResponseTableDTO revenueBreakdownResponseTableDTO = RevenueBreakdownResponseTableDTO.builder()
                .serviceProviderRevenue(round(providerRevenue, 2))
                .deliveryAgentRevenue(round(agentRevenue, 2))
                .adminRevenue(round(adminRevenue, 2))
                .build();

        return revenueBreakdownResponseTableDTO;
    }

    public RevenueBreakDownResponseGraphDTO getBreakdownGraph(Users user, String filter, LocalDate startDate, LocalDate endDate) {

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
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = now;
                break;
        }

        return revenueRepository.getRevenueBreakdownGroupedByDate(start, end);
    }

    public List<TotalRevenueDTO> getTotalRevenue(Users user, String filter, LocalDate startDate, LocalDate endDate) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Order> orders;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
        }

        List<TotalRevenueDTO> totalRevenueDTO = revenueRepository
                .getAdminRevenueBreakdownBetween(start, end);
        List<TotalRevenueDTO> resultList = new ArrayList<>();

        for(TotalRevenueDTO dto : totalRevenueDTO){

            TotalRevenueDTO revenueDTO = TotalRevenueDTO.builder()
                    .orderId(dto.getOrderId())
                    .date(dto.getDate())
                    .customerName(dto.getCustomerName())
                    .totalPaid(dto.getTotalPaid())
                    .providerPayout(round(dto.getProviderPayout(), 2))
                    .agentPayout(round(dto.getAgentPayout(), 2))
                    .adminRevenue(round(dto.getAdminRevenue(), 2))
                    .build();

            resultList.add(revenueDTO);
        }
     return resultList;
    }

    public OrderResponseDTO getOrderDetail(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not exists."));

        List<BookingItem> bookingItem = bookingItemRepository.findByOrder(order);

        List<OrderResponseDTO.BookingItemDTO> itemDTOS = new ArrayList<>();

        if(itemDTOS == null) {
            itemDTOS = null;
        }

        for(BookingItem items : bookingItem){
            OrderResponseDTO.BookingItemDTO bookingItemDTO = OrderResponseDTO.BookingItemDTO.builder()
                    .itemId(items.getBookingItemId())
                    .itemName(items.getItem().getItemName())
                    .quantity(items.getQuantity())
                    .finalPrice(items.getFinalPrice())
                    .build();

            itemDTOS.add(bookingItemDTO);
        }

        Bill bill = billRepository.findByOrder(order);

        if(bill == null){
            bill = null;
        }

        OrderResponseDTO.BillDTO billDTO = OrderResponseDTO.BillDTO.builder()
                .invoiceNumber(bill.getInvoiceNumber())
                .itemsTotalPrice(bill.getItemsTotalPrice())
                .deliveryCharge(bill.getDeliveryCharge())
                .gstAmount(bill.getGstAmount())
                .discountAmount(bill.getDiscountAmount())
                .finalPrice(bill.getFinalPrice())
                .build();

        Payment payment = paymentRepository.findByBill(bill);

        if(payment == null){
            payment = null;
        }

        OrderResponseDTO.PaymentDTO paymentDTO = OrderResponseDTO.PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .dateTime(payment.getDateTime())
                .transactionId(payment.getTransactionId())
                .build();

        List<Payout> payout = payoutRepository.findByPayment(payment);

        if(payout == null){
            payout = null;
        }

        List<OrderResponseDTO.PayoutDTO> payoutDTOS = new ArrayList<>();
        for(Payout payout1 : payout){
            OrderResponseDTO.PayoutDTO payoutDTO = OrderResponseDTO.PayoutDTO.builder()
                    .payoutId(payout1.getPayoutId())
                    .deliveryEarning(payout1.getDeliveryEarning())
                    .charge(payout1.getCharge())
                    .finalAmount(payout1.getFinalAmount())
                    .transactionId(payout1.getTransactionId())
                    .payoutStatus(payout1.getStatus())
                    .dateTime(payout1.getDateTime())
                    .build();

            payoutDTOS.add(payoutDTO);
        }

        AdminRevenue adminRevenue = adminRevenueRepository.findByPayment(payment);

        if(adminRevenue == null){
            adminRevenue = null;
        }

        OrderResponseDTO.AdminRevenueDTO adminRevenueDTO = OrderResponseDTO.AdminRevenueDTO.builder()
                .profitFromDeliveryAgent(adminRevenue.getProfitFromDeliveryAgent())
                .profitFromServiceProvider(adminRevenue.getProfitFromServiceProvider())
                .totalRevenue(adminRevenue.getTotalRevenue())
                .build();

        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                .customerName(order.getUsers().getFirstName() + " " + order.getUsers().getLastName())
                .providerName(order.getServiceProvider().getBusinessName())
                .pickupAgentName(order.getPickupDeliveryAgent() != null ? order.getPickupDeliveryAgent().getUsers().getFirstName() + " " + order.getPickupDeliveryAgent().getUsers().getLastName() : null)
                .deliveryAgentName (order.getDeliveryDeliveryAgent() != null ? order.getDeliveryDeliveryAgent().getUsers().getFirstName() + " " + order.getDeliveryDeliveryAgent().getUsers().getLastName() : null)
                .bookingItemDTOS(itemDTOS)
                .billDTO(billDTO)
                .paymentDTO(paymentDTO)
                .payoutDTOS(payoutDTOS)
                .adminRevenueDTO(adminRevenueDTO)
                .build();

        return orderResponseDTO;
    }

    // Return revenue trend graph details
    public RevenueTrendDTO getRevenueTrendsGraph(String type, String filter) {
        LocalDateTime now = LocalDateTime.now();
        List<RevenueGraphPointDTO> graphPoints = new ArrayList<>();
        String title;
        DateTimeFormatter formatter;
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (filter.toLowerCase()) {
                case "yearly" :
                    title = "Yearly " + formatTypeTitle(type);
                    for (int i = 1; i <= now.getMonthValue(); i++) {
                        start = LocalDate.of(now.getYear(), i, 1).atStartOfDay();
                        end = start.plusMonths(1);
                        String label = start.getMonth().name().substring(0, 3); // "Jan", "Feb", etc.
                        Double value = getRevenueByType(type, start, end);
                        graphPoints.add(new RevenueGraphPointDTO(label, value != null ? round(value, 2) : 0.0));
                    }
                    break;

            case "quarterly" :
                title = "Quarterly " + formatTypeTitle(type);
                YearMonth current = YearMonth.from(now);
                for (int i = 3; i >= 0; i--) {
                    YearMonth quarterMonth = current.minusMonths(i * 3L);
                    int year = quarterMonth.getYear();
                    int q = ((quarterMonth.getMonthValue() - 1) / 3) + 1;
                    start = LocalDate.of(year, ((q - 1) * 3 + 1), 1).atStartOfDay();
                    end = start.plusMonths(3);
                    String label = "Q" + q + " " + year;
                    Double value = getRevenueByType(type, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, value != null ? round(value, 2) : 0.0));
                }
                break;

            case "monthly" :
            default :
                title = "Monthly " + formatTypeTitle(type);
                int currentYear = now.getYear();
                int currentMonth = now.getMonthValue();
                YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
                int daysInMonth = yearMonth.lengthOfMonth();

                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate date = LocalDate.of(currentYear, currentMonth, day);
                    start = date.atStartOfDay();
                    end = start.plusDays(1);

                    String label = String.format("%02d %s", day, date.getMonth().name().substring(0, 3)); // e.g., "01 Jun"
                    Double value = getRevenueByType(type, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, value != null ? round(value, 2) : 0.0));
                }
                break;


        }

        return new RevenueTrendDTO(title, graphPoints);
    }

    private Double getRevenueByType(String type, LocalDateTime start, LocalDateTime end) {
        return switch (type.toLowerCase()) {
            case "gross sales" -> billRepository.getGrossSalesBetween(start, end);
            case "admin revenue" -> adminRevenueRepository.getAdminRevenueBetween(start, end);
            case "provider payout" -> payoutRepository.getProviderPayoutBetween(start, end);
            case "delivery payout" -> payoutRepository.getDeliveryPayoutBetween(start, end);
            case "total payout" -> payoutRepository.getTotalPayoutBetween(start, end);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }

    private String formatTypeTitle(String type) {
        return switch (type.toLowerCase()) {
            case "gross sales" -> "Gross Sales";
            case "admin revenue" -> "Admin Revenue";
            case "provider payout" -> "Provider Payout";
            case "delivery payout" -> "Delivery Payout";
            case "total payout" -> "Total Payouts";
            default -> "Revenue";
        };
    }

    public InsightResponseDTO getInsights(String filter) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (filter.toLowerCase()) {
            case "yearly":
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                break;
            case "quarterly":
                int month = LocalDate.now().getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(LocalDate.now().getYear(), startMonth, 1).atStartOfDay();
                break;
            case "monthly":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            case "daily":
            default:
                start = LocalDate.now().atStartOfDay();
                break;
        }

        List<ServiceProviderInsightDTO> topProviders =
                payoutRepository.findTopServiceProviderByPayoutInRangeNative(start, end);
        ServiceProviderInsightDTO topProvider = topProviders.isEmpty()
                ? new ServiceProviderInsightDTO("N/A", 0.0)
                : topProviders.get(0);

        // Get top delivery agents
        List<DeliveryAgentInsightDTO> deliveryAgents = orderRepository.findTopDeliveryAgentsInRange(start, end);

        List<DeliveryAgentInsightDTO> topAgents = new ArrayList<>();

        if (!deliveryAgents.isEmpty()) {
            Long maxDeliveries = deliveryAgents.get(0).getDeliveries(); // highest count
            topAgents = deliveryAgents.stream()
                    .filter(agent -> agent.getDeliveries().equals(maxDeliveries))
                    .collect(Collectors.toList());
        } else {
            topAgents.add(new DeliveryAgentInsightDTO("N/A", 0L));
        }


        OrderInsightDTO orderInsightDTO = orderRepository.findTopOrderByValueBetweenDates(start, end);

        InsightResponseDTO insightResponseDTO = InsightResponseDTO.builder()
                .businessName(topProvider.getBusinessName())
                .sales(topProvider.getSales())
                .agentName(topAgents.getFirst().getAgentName())
                .deliveries(topAgents.getFirst().getDeliveries())
                .orderId(orderInsightDTO.getOrderId())
                .orderValue(orderInsightDTO.getOrderValue())
                .build();

        return insightResponseDTO;
    }

    public List<ServiceProviderRevenueTableDTO> getProviderRevenueTable(String filter) {

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch(filter.toLowerCase()){
            case "yearly" :
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                break;
            case "quarterly":
                int month = LocalDate.now().getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(LocalDate.now().getYear(), startMonth, 1).atStartOfDay();
                break;
            case "monthly" :
            default :
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }

        List<ServiceProviderRevenueTableDTO> serviceProviderRevenueTableDTOS = payoutRepository
                .findAllProviderRevenuesInRange(start, end);

        // start and end date added to each result
        serviceProviderRevenueTableDTOS.forEach(dto -> {
            dto.setStartDate(start);
            dto.setEndDate(end);
        });

        return serviceProviderRevenueTableDTOS;
    }

    public ServiceProviderRevenueGraphDTO getProviderRevenueGraph(String filter, String providerId) {

        LocalDateTime now = LocalDateTime.now();
        List<RevenueGraphPointDTO> graphPoints = new ArrayList<>();
        String title;

        ServiceProvider serviceProvider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found."));

        String userId = serviceProvider.getUser().getUserId();

        switch (filter.toLowerCase()) {
            case "yearly":
                title = "Yearly Revenue for service provider : " + serviceProvider.getUser().getFirstName() + " " + serviceProvider.getUser().getLastName();
                for (int i = 1; i <= now.getMonthValue(); i++) {
                    LocalDateTime start = LocalDate.of(now.getYear(), i, 1).atStartOfDay();
                    LocalDateTime end = start.plusMonths(1);
                    String label = start.getMonth().name().substring(0, 3); // "Jan", "Feb", etc.
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, revenue != null ? round(revenue, 2) : 0.0));
                }
                break;

            case "quarterly":
                title = "Quarterly Revenue for service provider : " + serviceProvider.getUser().getFirstName() + " " + serviceProvider.getUser().getLastName();
                YearMonth current = YearMonth.from(now);
                for (int i = 3; i >= 0; i--) {
                    YearMonth quarterMonth = current.minusMonths(i * 3L);
                    int q = ((quarterMonth.getMonthValue() - 1) / 3) + 1;
                    LocalDateTime start = LocalDate.of(quarterMonth.getYear(), ((q - 1) * 3 + 1), 1).atStartOfDay();
                    LocalDateTime end = start.plusMonths(3);
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO("Q" + q + " " + start.getYear(), revenue != null ? revenue : 0.0));
                }
                break;

            case "monthly":
            default:
                title = "Monthly Revenue for service provider : " + serviceProvider.getUser().getFirstName() + " " + serviceProvider.getUser().getLastName();
                int currentYear = now.getYear();
                int currentMonth = now.getMonthValue();
                YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
                int daysInMonth = yearMonth.lengthOfMonth();

                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate date = LocalDate.of(currentYear, currentMonth, day);
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = start.plusDays(1);

                    String label = String.format("%02d %s", day, date.getMonth().name().substring(0, 3)); // e.g., "01 Jun"
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, revenue != null ? round(revenue, 2) : 0.0));
                }
                break;
        }

        return ServiceProviderRevenueGraphDTO.builder()
                .title(title)
                .data(graphPoints)
                .build();
    }

    public List<DeliveryAgentRevenueTableDTO> getAgentRevenueTable(String filter, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch(filter.toLowerCase()){
            case "yearly" :
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                break;
            case "quarterly":
                int month = LocalDate.now().getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(LocalDate.now().getYear(), startMonth, 1).atStartOfDay();
                break;
            case "monthly" :
            default :
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }

        List<DeliveryAgentRevenueTableDTO> deliveryAgentRevenueTableDTOS = payoutRepository
                .findAllAgentRevenuesInRange(start, end);

        // start and end date added to each result
        deliveryAgentRevenueTableDTOS.forEach(dto -> {
            dto.setStartDate(start);
            dto.setEndDate(end);
        });

        return deliveryAgentRevenueTableDTOS;
    }

    public DeliveryAgentRevenueGraphDTO getAgentRevenueGraph(String filter, String agentId) {

        LocalDateTime now = LocalDateTime.now();
        List<RevenueGraphPointDTO> graphPoints = new ArrayList<>();
        String title;

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Delivery agent not found."));

        String userId = deliveryAgent.getUsers().getUserId();

        switch (filter.toLowerCase()) {
            case "yearly":
                title = "Yearly Revenue for delivery agent : " + deliveryAgent.getUsers().getFirstName() + " " + deliveryAgent.getUsers().getLastName();

                for (int i = 1; i <= now.getMonthValue(); i++) {
                    LocalDateTime start = LocalDate.of(now.getYear(), i, 1).atStartOfDay();
                    LocalDateTime end = start.plusMonths(1);
                    String label = start.getMonth().name().substring(0, 3); // "Jan", "Feb", etc.
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, revenue != null ? round(revenue, 2) : 0.0));
                }

//                for (int i = 4; i >= 0; i--) {
//                    int year = now.minusYears(i).getYear();
//                    LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
//                    LocalDateTime end = start.plusYears(1);
//                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
//                    graphPoints.add(new RevenueGraphPointDTO(String.valueOf(year), revenue != null ? revenue : 0.0));
//                }
                break;

            case "quarterly":
                title = "Quarterly Revenue for delivery agent : " + deliveryAgent.getUsers().getFirstName() + " " + deliveryAgent.getUsers().getLastName();
                YearMonth current = YearMonth.from(now);
                for (int i = 3; i >= 0; i--) {
                    YearMonth quarterMonth = current.minusMonths(i * 3L);
                    int q = ((quarterMonth.getMonthValue() - 1) / 3) + 1;
                    LocalDateTime start = LocalDate.of(quarterMonth.getYear(), ((q - 1) * 3 + 1), 1).atStartOfDay();
                    LocalDateTime end = start.plusMonths(3);
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO("Q" + q + " " + start.getYear(), revenue != null ? revenue : 0.0));
                }
                break;

            case "monthly":
            default:
                title = "Monthly Revenue for delivery agent : " + deliveryAgent.getUsers().getFirstName() + " " + deliveryAgent.getUsers().getLastName();

                int currentYear = now.getYear();
                int currentMonth = now.getMonthValue();
                YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
                int daysInMonth = yearMonth.lengthOfMonth();

                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate date = LocalDate.of(currentYear, currentMonth, day);
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = start.plusDays(1);

                    String label = String.format("%02d %s", day, date.getMonth().name().substring(0, 3)); // e.g., "01 Jun"
                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
                    graphPoints.add(new RevenueGraphPointDTO(label, revenue != null ? round(revenue, 2) : 0.0));
                }

//                for (int i = 1; i <= now.getMonthValue(); i++) {
//                    LocalDateTime start = LocalDate.of(now.getYear(), i, 1).atStartOfDay();
//                    LocalDateTime end = start.plusMonths(1);
//                    Double revenue = payoutRepository.getRevenueForUserInRange(userId, start, end);
//                    graphPoints.add(new RevenueGraphPointDTO(start.getMonth().name().substring(0, 3), revenue != null ? revenue : 0.0));
//                }
                break;
        }

        return DeliveryAgentRevenueGraphDTO.builder()
                .title(title)
                .data(graphPoints)
                .build();
    }


//    public List<RevenueTrendDTO> getRevenueTrendsGraph(String type, String filter) {
//
//        List<RevenueTrendDTO> trends = new ArrayList<>();
//        LocalDate today = LocalDate.now();
//
//        switch (filter.toLowerCase()) {
//            case "quarterly" :
//                for (int i = 0; i < 4; i++) {
//                    LocalDate end = today.minusMonths(i * 3).withDayOfMonth(1).plusMonths(3).minusDays(1);
//                    LocalDate start = end.minusMonths(2).withDayOfMonth(1);
//                    String label = "Q" + ((start.getMonthValue() - 1) / 3 + 1) + " " + start.getYear();
//                    trends.add(getTrendForPeriod(start, end, label));
//                }
//                break;
//            case "yearly" :
//                for (int i = 0; i < 5; i++) {
//                    LocalDate start = LocalDate.of(today.getYear() - i, 1, 1);
//                    LocalDate end = start.withMonth(12).withDayOfMonth(31);
//                    trends.add(getTrendForPeriod(start, end, String.valueOf(start.getYear())));
//                }
//                break;
//            case "monthly" :
//            default :
//                for (int i = 0; i < 12; i++) {
//                    LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
//                    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
//                    trends.add(getTrendForPeriod(monthStart, monthEnd, String.valueOf(DateTimeFormatter.ofPattern("MMM yyyy"))));
//                }
//        }
//
//        Collections.reverse(trends); // Optional: to show oldest to newest
//        return trends;
//    }
//
//
//    private RevenueTrendDTO getTrendForPeriod(LocalDate start, LocalDate end, String label) {
//        LocalDateTime startDateTime = start.atStartOfDay();
//        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
//
//        BigDecimal grossSales = billRepository.sumGrossSalesByDateRange(startDateTime, endDateTime);
//        BigDecimal deliveryPayouts = paymentRepository.sumDeliveryPayoutsInRange(startDateTime, endDateTime);
//        BigDecimal serviceProviderPayouts = paymentRepository.sumServiceProviderPayoutsInRange(startDateTime, endDateTime);
//
//
////        BigDecimal grossSales = orderRepository.sumTotalAmountByDateRange(startDateTime, endDateTime);
////        BigDecimal deliveryPayouts = payoutRepository.sumDeliveryAgentPayouts(startDateTime, endDateTime);
////        BigDecimal serviceProviderPayouts = payoutRepository.sumServiceProviderPayouts(startDateTime, endDateTime);
//
//        BigDecimal totalPayouts = deliveryPayouts.add(serviceProviderPayouts);
//        BigDecimal adminRevenue = grossSales.subtract(totalPayouts);
//
//        return new RevenueTrendDTO(
//                label,
//                grossSales != null ? grossSales : BigDecimal.ZERO,
//                totalPayouts != null ? totalPayouts : BigDecimal.ZERO,
//                deliveryPayouts != null ? deliveryPayouts : BigDecimal.ZERO,
//                serviceProviderPayouts != null ? serviceProviderPayouts : BigDecimal.ZERO,
//                adminRevenue != null ? adminRevenue : BigDecimal.ZERO
//        );
//    }

}
