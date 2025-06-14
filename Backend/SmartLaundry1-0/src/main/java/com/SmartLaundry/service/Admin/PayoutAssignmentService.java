package com.SmartLaundry.service.Admin;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.AdminRevenueRepository;
import com.SmartLaundry.repository.PayoutRepository;
import com.SmartLaundry.repository.RevenueBreakDownRepository;
import com.SmartLaundry.repository.UserRepository;
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

    public void addPayouts(Payment payment) {

        Double agentCharge = payment.getBill().getDeliveryCharge();
        Double providerCharge = payment.getBill().getFinalPrice() - payment.getBill().getDeliveryCharge();

        RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);

        Double adminAgentRevenue, adminProviderRevenue;

        adminAgentRevenue = agentCharge * (revenueBreakDown.getDeliveryAgent() / 100);
        agentCharge = agentCharge - adminAgentRevenue;

        adminProviderRevenue = providerCharge * (revenueBreakDown.getServiceProvider() / 100);
        providerCharge = providerCharge - adminProviderRevenue;

        Double deliveryAgentRevenue = (adminAgentRevenue * 2), serviceProviderRevenue = adminProviderRevenue, totalRevenue = 0.0;

        if(payment.getBill().getDeliveryCharge() != null){
            Payout pickupAgent = Payout.builder()
                    .payment(payment)
                    .deliveryEarning(payment.getBill().getDeliveryCharge())
                    .charge(adminAgentRevenue)
                    .finalAmount(agentCharge)
                    .users(payment.getBill().getOrder().getPickupDeliveryAgent().getUsers())
                    .build();

            payoutRepository.save(pickupAgent);

            Payout deliveryAgent = Payout.builder()
                    .payment(payment)
                    .deliveryEarning(payment.getBill().getDeliveryCharge())
                    .charge(adminAgentRevenue)
                    .finalAmount(agentCharge)
                    .users(payment.getBill().getOrder().getDeliveryDeliveryAgent().getUsers())
                    .build();

            payoutRepository.save(deliveryAgent);


        }

        Payout payoutForServiceProvider = Payout.builder()
                .payment(payment)
                .deliveryEarning(payment.getBill().getFinalPrice() - payment.getBill().getDeliveryCharge())
                .charge(adminProviderRevenue)
                .finalAmount(providerCharge)
                .users(payment.getBill().getOrder().getServiceProvider().getUser())
                .build();

        payoutRepository.save(payoutForServiceProvider);

        totalRevenue = deliveryAgentRevenue + serviceProviderRevenue;

        AdminRevenue adminRevenue = AdminRevenue.builder()
                .payment(payment)
                .profitFromDeliveryAgent(deliveryAgentRevenue)
                .profitFromServiceProvider(serviceProviderRevenue)
                .totalRevenue(totalRevenue)
                .build();

        adminRevenueRepository.save(adminRevenue);

    }

}
