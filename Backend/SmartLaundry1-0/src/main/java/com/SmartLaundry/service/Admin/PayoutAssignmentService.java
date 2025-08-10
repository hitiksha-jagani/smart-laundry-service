package com.SmartLaundry.service.Admin;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.twilio.twiml.voice.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;
import java.time.LocalDateTime;

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

    public void addPayouts(Payment payment) {
        try {
            Bill bill = payment.getBill();
            if (bill == null || bill.getOrder() == null) {
                System.err.println("Bill or Order is null for payment: " + payment.getPaymentId());
                return;
            }

            Order order = bill.getOrder();

            Double deliveryCharge = bill.getDeliveryCharge() != null ? bill.getDeliveryCharge() : 0.0; // 60
            Double halfDeliveryCharge = deliveryCharge / 2; // 30
            Double finalPrice = bill.getFinalPrice() != null ? bill.getFinalPrice() : 0.0; // 119
            Double servicePrice = bill.getItemsTotalPrice(); // 50

            RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
            if (revenueBreakDown == null) {
                System.err.println("RevenueBreakDown not found!");
                return;
            }

            Double adminAgentPercent = revenueBreakDown.getDeliveryAgent(); // 20%
            Double adminProviderPercent = revenueBreakDown.getServiceProvider(); // 40%

            Double adminAgentRevenue = halfDeliveryCharge * (adminAgentPercent / 100); // 30*20% = 6
            Double adminProviderRevenue = servicePrice * (adminProviderPercent / 100); // 50*40% = 20

            Double agentEarning = halfDeliveryCharge - adminAgentRevenue; // 30 - 6 = 24
            Double providerEarning = 0.0;

            ServiceProvider serviceProvider = order.getServiceProvider();
            if (serviceProvider == null) {
                System.err.println("ServiceProvider is null for order: " + order.getOrderId());
                return;
            }

            if (Boolean.TRUE.equals(serviceProvider.getNeedOfDeliveryAgent())) {
                providerEarning = (servicePrice - adminProviderRevenue) - adminAgentRevenue; // (50 - 20) - 6 = 30 - 6 = 24
                adminProviderRevenue = adminProviderRevenue + adminAgentRevenue; // 20 + 6 = 26
                if (providerEarning < 0) {
                    providerEarning = 0.0;
                }
            } else {
                providerEarning = servicePrice - adminProviderRevenue; // 50-20 = 30
                if (providerEarning < 0) {
                    providerEarning = 0.0;
                }
            }

            // ---- Payouts ----
            if (deliveryCharge > 0) {
                Users pickupUser = order.getPickupDeliveryAgent() != null ? order.getPickupDeliveryAgent().getUsers() : null;
                Users deliveryUser = order.getDeliveryDeliveryAgent() != null ? order.getDeliveryDeliveryAgent().getUsers() : null;

                if (pickupUser != null) {
                    Payout pickupAgent = Payout.builder()
                            .payment(payment)
                            .deliveryEarning(halfDeliveryCharge) // 30
                            .charge(adminAgentRevenue) // 6
                            .finalAmount(agentEarning) // 24
                            .status(PayoutStatus.PENDING)
                            .users(pickupUser)
                            .build();
                    System.out.println("Pickup agent payout : " + pickupAgent.getFinalAmount());
                    payoutRepository.save(pickupAgent);
                }

                if (deliveryUser != null) {
                    Payout deliveryAgent = Payout.builder()
                            .payment(payment)
                            .deliveryEarning(halfDeliveryCharge) // 30
                            .charge(adminAgentRevenue) // 6
                            .finalAmount(agentEarning) // 24
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
                        .deliveryEarning(servicePrice) // 50
                        .charge(adminProviderRevenue) // 20/26
                        .finalAmount(providerEarning) // 30/24
                        .status(PayoutStatus.PENDING)
                        .users(serviceProvider.getUser())
                        .build();
                System.out.println("Service provider payout : " + providerPayout.getFinalAmount());
                payoutRepository.save(providerPayout);
            }

            Double adminTotalRevenue = (adminAgentRevenue * 2) + adminProviderRevenue; // (6 * 2) + 26/20 = 38/32

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
        }
    }
}
