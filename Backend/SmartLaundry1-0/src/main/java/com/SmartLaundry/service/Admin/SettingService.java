package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ComplaintCategoryRepository;
import com.SmartLaundry.repository.DeliveryAgentEarningsRepository;
import com.SmartLaundry.repository.GeocodingConfigRepository;
import com.SmartLaundry.repository.RevenueBreakDownRepository;
import com.twilio.rest.monitor.v1.Alert;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SettingService {

    @Autowired
    private ComplaintCategoryRepository complaintCategoryRepository;

    @Autowired
    private RevenueBreakDownRepository revenueBreakDownRepository;

    @Autowired
    private DeliveryAgentEarningsRepository deliveryAgentEarningsRepository;

    @Autowired
    private GeocodingConfigRepository configRepository;

    public void saveConfig(String apiProvider, String apiKey, String userId) {
        GeocodingConfig config = new GeocodingConfig(apiProvider, apiKey, userId);
        configRepository.save(config);
    }

    public Optional<GeocodingConfig> getLatestConfig() {
        return Optional.ofNullable(configRepository.findTopByOrderByCreatedAtDesc());
    }

    public List<GeocodingConfig> getAllConfigs() {
        return configRepository.findAllByOrderByCreatedAtDesc();
    }

    public String getCurrentProvider() {
        return getLatestConfig().map(GeocodingConfig::getApiProvider).orElse("Open Cage");
    }

    public String getCurrentApiKey() {
        return getLatestConfig().map(GeocodingConfig::getApiKey).orElse("");
    }

    // Set complaint category
    public ComplaintCategoryResponseDTO setComplaintCategory(ComplaintCategorySetDTO complaintCategorySetDTO) {

        ComplaintCategory complaintCategory = ComplaintCategory.builder()
                .categoryName(complaintCategorySetDTO.getCategory())
                .build();

        complaintCategoryRepository.save(complaintCategory);

        return new ComplaintCategoryResponseDTO(complaintCategorySetDTO.getCategory(), "Category set successfully.");
    }

    // Set revenue breakdown
    public RevenueSettingResponseDTO setRevenue(RevenueSettingRequestDTO revenueSettingRequestDTO) {

        RevenueBreakDown revenueBreakDown = RevenueBreakDown.builder()
                .deliveryAgent(revenueSettingRequestDTO.getDeliveryAgentRevenue())
                .serviceProvider(revenueSettingRequestDTO.getServiceProviderRevenue())
                .build();

        revenueBreakDownRepository.save(revenueBreakDown);

        return new RevenueSettingResponseDTO(revenueSettingRequestDTO.getServiceProviderRevenue(),
                revenueSettingRequestDTO.getDeliveryAgentRevenue(),
                "Revenue is set as Delivery Agent : " + revenueSettingRequestDTO.getDeliveryAgentRevenue()
                        + "% Service Provider : " + revenueSettingRequestDTO.getServiceProviderRevenue() + "%");
    }

    public String setAgentEarnings(@Valid DeliveryAgentEarningSettingRequestDTO deliveryAgentEarningSettingRequestDTO) {

        CurrentStatus status = deliveryAgentEarningSettingRequestDTO.getCurrentStatus();
        LocalDateTime active = null;

        // Raise popup for confirmation( logic is available in frontend for that )
        if(status.equals(CurrentStatus.ACTIVE)){

            active = LocalDateTime.now();

            DeliveryAgentEarnings earnings = deliveryAgentEarningsRepository.findByCurrentStatus(CurrentStatus.ACTIVE);

            if(earnings != null){
                earnings.setCurrentStatus(CurrentStatus.INACTIVE);
                earnings.setDeactivateAt(LocalDateTime.now());
                deliveryAgentEarningsRepository.save(earnings);
            }

        }

        DeliveryAgentEarnings deliveryAgentEarnings = DeliveryAgentEarnings.builder()
                .baseKm(deliveryAgentEarningSettingRequestDTO.getBaseKm() != null ? deliveryAgentEarningSettingRequestDTO.getBaseKm() : null)
                .fixedAmount(deliveryAgentEarningSettingRequestDTO.getFixedAmount())
                .extraPerKmAmount(deliveryAgentEarningSettingRequestDTO.getExtraPerKmAmount() != null ? deliveryAgentEarningSettingRequestDTO.getExtraPerKmAmount() : null)
                .currentStatus(status)
                .activeAt(active)
                .deactivateAt(null)
                .build();

        deliveryAgentEarningsRepository.save(deliveryAgentEarnings);

        return "Delivery agent earnings set successfully.";

    }

    public List<DeliveryAgentEarnings> getAgentEarnings() {

        List<DeliveryAgentEarnings> deliveryAgentEarnings = deliveryAgentEarningsRepository.findAll();
        return deliveryAgentEarnings;

    }

    public String changeAgentEarningStatus(Long id) {

        DeliveryAgentEarnings deliveryAgentEarnings = deliveryAgentEarningsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Earnings not available."));

        if(deliveryAgentEarnings.getCurrentStatus() == CurrentStatus.INACTIVE){
            deliveryAgentEarnings.setCurrentStatus(CurrentStatus.ACTIVE);
            deliveryAgentEarnings.setActiveAt(LocalDateTime.now());
        } else {
            deliveryAgentEarnings.setCurrentStatus(CurrentStatus.INACTIVE);
            deliveryAgentEarnings.setDeactivateAt(LocalDateTime.now());
        }

        deliveryAgentEarningsRepository.save(deliveryAgentEarnings);

        return "Status of " + deliveryAgentEarnings.getEarningId() + "is changed successfully.";
    }
}
