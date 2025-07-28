package com.SmartLaundry.service.Admin;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.twilio.twiml.voice.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;

@Service
public class PayoutAssignmentService {

    @Autowired
    private PayoutRepository payoutRepository;

    @Autowired
    private RevenueBreakDownRepository revenueBreakDownRepository;

    @Autowired
    private AdminRevenueRepository adminRevenueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
//Before logic : Hitiksha
//    public void addPayouts(Payment payment) {
//        Double deliveryCharge = payment.getBill().getDeliveryCharge();
//        Double finalPrice = payment.getBill().getFinalPrice();
//        Double servicePrice = finalPrice - deliveryCharge;
//
//        RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
//
//        Double adminAgentPercent = revenueBreakDown.getDeliveryAgent();
//        Double adminProviderPercent = revenueBreakDown.getServiceProvider();
//
//        // Admin cut
//        Double adminAgentRevenue = deliveryCharge * (adminAgentPercent / 100);
//        Double adminProviderRevenue = servicePrice * (adminProviderPercent / 100);
//
//        // Earnings
//        Double agentEarning = deliveryCharge - adminAgentRevenue;
//        Double providerEarning = servicePrice - adminProviderRevenue;
//
//        // Fetch provider
//        ServiceProvider serviceProvider = serviceProviderRepository.findById(payment.getBill().getOrder().getServiceProvider().getServiceProviderId())
//                .orElseThrow(() -> new RuntimeException("Service provider not exist."));
//
//        if (serviceProvider.getNeedOfDeliveryAgent() != null && serviceProvider.getNeedOfDeliveryAgent()) {
//            // Provider bears the delivery agent cost → deduct deliveryCharge from provider's payout
//            providerEarning = providerEarning - deliveryCharge;
//        }
//
//        // ---- Payouts ----
//        if (deliveryCharge != null && deliveryCharge > 0) {
//            Payout pickupAgent = Payout.builder()
//                    .payment(payment)
//                    .deliveryEarning(deliveryCharge)
//                    .charge(adminAgentRevenue)
//                    .finalAmount(agentEarning)
//                    .status(PayoutStatus.PENDING)
//                    .users(payment.getBill().getOrder().getPickupDeliveryAgent().getUsers())
//                    .build();
//
//            System.out.println("Pickup agent payout : " + pickupAgent.getFinalAmount());
//            payoutRepository.save(pickupAgent);
//
//            Payout deliveryAgent = Payout.builder()
//                    .payment(payment)
//                    .deliveryEarning(deliveryCharge)
//                    .charge(adminAgentRevenue)
//                    .finalAmount(agentEarning)
//                    .status(PayoutStatus.PENDING)
//                    .users(payment.getBill().getOrder().getDeliveryDeliveryAgent().getUsers())
//                    .build();
//
//            System.out.println("Delivery agent payout : " + deliveryAgent.getFinalAmount());
//            payoutRepository.save(deliveryAgent);
//        }
//
//        Payout providerPayout = Payout.builder()
//                .payment(payment)
//                .deliveryEarning(servicePrice)
//                .charge(adminProviderRevenue)
//                .finalAmount(providerEarning)
//                .status(PayoutStatus.PENDING)
//                .users(payment.getBill().getOrder().getServiceProvider().getUser())
//                .build();
//
//        System.out.println("Service provider payout : " + providerPayout.getFinalAmount());
//        payoutRepository.save(providerPayout);
//
//        Double adminTotalRevenue = adminAgentRevenue + adminProviderRevenue;
//
//        AdminRevenue adminRevenue = AdminRevenue.builder()
//                .payment(payment)
//                .profitFromDeliveryAgent(adminAgentRevenue)
//                .profitFromServiceProvider(adminProviderRevenue)
//                .totalRevenue(adminTotalRevenue)
//                .build();
//
//        System.out.println("Admin revenue : " + adminRevenue.getTotalRevenue());
//        adminRevenueRepository.save(adminRevenue);
//    }
    public void addPayouts(Payment payment) {
        try {
            Bill bill = payment.getBill();
            if (bill == null || bill.getOrder() == null) {
                System.err.println("Bill or Order is null for payment: " + payment.getPaymentId());
                return;
            }

            Order order = bill.getOrder();

            Double deliveryCharge = bill.getDeliveryCharge() != null ? bill.getDeliveryCharge() : 0.0;
            Double finalPrice = bill.getFinalPrice() != null ? bill.getFinalPrice() : 0.0;
            Double servicePrice = finalPrice - deliveryCharge;

            RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
            if (revenueBreakDown == null) {
                System.err.println("RevenueBreakDown not found!");
                return;
            }

            Double adminAgentPercent = revenueBreakDown.getDeliveryAgent();
            Double adminProviderPercent = revenueBreakDown.getServiceProvider();

            Double adminAgentRevenue = deliveryCharge * (adminAgentPercent / 100);
            Double adminProviderRevenue = servicePrice * (adminProviderPercent / 100);

            Double agentEarning = deliveryCharge - adminAgentRevenue;
            Double providerEarning = servicePrice - adminProviderRevenue;

            ServiceProvider serviceProvider = order.getServiceProvider();
            if (serviceProvider == null) {
                System.err.println("ServiceProvider is null for order: " + order.getOrderId());
                return;
            }

            if (Boolean.TRUE.equals(serviceProvider.getNeedOfDeliveryAgent())) {
                providerEarning = providerEarning - deliveryCharge;
            }

            // ---- Payouts ----
            if (deliveryCharge > 0) {
                Users pickupUser = order.getPickupDeliveryAgent() != null ? order.getPickupDeliveryAgent().getUsers() : null;
                Users deliveryUser = order.getDeliveryDeliveryAgent() != null ? order.getDeliveryDeliveryAgent().getUsers() : null;

                if (pickupUser != null) {
                    Payout pickupAgent = Payout.builder()
                            .payment(payment)
                            .deliveryEarning(deliveryCharge)
                            .charge(adminAgentRevenue)
                            .finalAmount(agentEarning)
                            .status(PayoutStatus.PENDING)
                            .users(pickupUser)
                            .build();
                    System.out.println("Pickup agent payout : " + pickupAgent.getFinalAmount());
                    payoutRepository.save(pickupAgent);
                }

                if (deliveryUser != null) {
                    Payout deliveryAgent = Payout.builder()
                            .payment(payment)
                            .deliveryEarning(deliveryCharge)
                            .charge(adminAgentRevenue)
                            .finalAmount(agentEarning)
                            .status(PayoutStatus.PENDING)
                            .users(deliveryUser)
                            .build();
                    System.out.println("Delivery agent payout : " + deliveryAgent.getFinalAmount());
                    payoutRepository.save(deliveryAgent);
                }
            }

            if (serviceProvider.getUser() != null) {
                Payout providerPayout = Payout.builder()
                        .payment(payment)
                        .deliveryEarning(servicePrice)
                        .charge(adminProviderRevenue)
                        .finalAmount(providerEarning)
                        .status(PayoutStatus.PENDING)
                        .users(serviceProvider.getUser())
                        .build();
                System.out.println("Service provider payout : " + providerPayout.getFinalAmount());
                payoutRepository.save(providerPayout);
            }

            Double adminTotalRevenue = adminAgentRevenue + adminProviderRevenue;

            AdminRevenue adminRevenue = AdminRevenue.builder()
                    .payment(payment)
                    .profitFromDeliveryAgent(adminAgentRevenue)
                    .profitFromServiceProvider(adminProviderRevenue)
                    .totalRevenue(adminTotalRevenue)
                    .build();

            System.out.println("Admin revenue : " + adminRevenue.getTotalRevenue());
            adminRevenueRepository.save(adminRevenue);

        } catch (Exception e) {
            System.err.println("❌ Error during payout assignment for paymentId: " + payment.getPaymentId());
            e.printStackTrace();
            // Optional: log to file or monitoring tool
        }
    }
}
