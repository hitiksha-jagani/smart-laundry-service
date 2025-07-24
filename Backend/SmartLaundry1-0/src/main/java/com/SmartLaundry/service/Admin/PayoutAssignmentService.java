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

    public void addPayouts(Payment payment) {
        Double deliveryCharge = payment.getBill().getDeliveryCharge();
        Double finalPrice = payment.getBill().getFinalPrice();
        Double servicePrice = finalPrice - deliveryCharge;

        RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);

        Double adminAgentPercent = revenueBreakDown.getDeliveryAgent();
        Double adminProviderPercent = revenueBreakDown.getServiceProvider();

        // Admin cut
        Double adminAgentRevenue = deliveryCharge * (adminAgentPercent / 100);
        Double adminProviderRevenue = servicePrice * (adminProviderPercent / 100);

        // Earnings
        Double agentEarning = deliveryCharge - adminAgentRevenue;
        Double providerEarning = servicePrice - adminProviderRevenue;

        // Fetch provider
        ServiceProvider serviceProvider = serviceProviderRepository.findById(payment.getBill().getOrder().getServiceProvider().getServiceProviderId())
                .orElseThrow(() -> new RuntimeException("Service provider not exist."));

        if (serviceProvider.getNeedOfDeliveryAgent() != null && serviceProvider.getNeedOfDeliveryAgent()) {
            // Provider bears the delivery agent cost → deduct deliveryCharge from provider's payout
            providerEarning = providerEarning - deliveryCharge;
        }

        // ---- Payouts ----
        if (deliveryCharge != null && deliveryCharge > 0) {
            Payout pickupAgent = Payout.builder()
                    .payment(payment)
                    .deliveryEarning(deliveryCharge)
                    .charge(adminAgentRevenue)
                    .finalAmount(agentEarning)
                    .status(PayoutStatus.PENDING)
                    .users(payment.getBill().getOrder().getPickupDeliveryAgent().getUsers())
                    .build();

            System.out.println("Pickup agent payout : " + pickupAgent.getFinalAmount());
            payoutRepository.save(pickupAgent);

            Payout deliveryAgent = Payout.builder()
                    .payment(payment)
                    .deliveryEarning(deliveryCharge)
                    .charge(adminAgentRevenue)
                    .finalAmount(agentEarning)
                    .status(PayoutStatus.PENDING)
                    .users(payment.getBill().getOrder().getDeliveryDeliveryAgent().getUsers())
                    .build();

            System.out.println("Delivery agent payout : " + deliveryAgent.getFinalAmount());
            payoutRepository.save(deliveryAgent);
        }

        Payout providerPayout = Payout.builder()
                .payment(payment)
                .deliveryEarning(servicePrice)
                .charge(adminProviderRevenue)
                .finalAmount(providerEarning)
                .status(PayoutStatus.PENDING)
                .users(payment.getBill().getOrder().getServiceProvider().getUser())
                .build();

        System.out.println("Service provider payout : " + providerPayout.getFinalAmount());
        payoutRepository.save(providerPayout);

        Double adminTotalRevenue = adminAgentRevenue + adminProviderRevenue;

        AdminRevenue adminRevenue = AdminRevenue.builder()
                .payment(payment)
                .profitFromDeliveryAgent(adminAgentRevenue)
                .profitFromServiceProvider(adminProviderRevenue)
                .totalRevenue(adminTotalRevenue)
                .build();

        System.out.println("Admin revenue : " + adminRevenue.getTotalRevenue());
        adminRevenueRepository.save(adminRevenue);
    }

}
