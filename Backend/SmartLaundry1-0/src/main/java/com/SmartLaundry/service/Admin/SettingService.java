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

    public void saveConfig(GeocodingConfig geocodingConfig, Users user) {

        Boolean activeStatus = geocodingConfig.isActiveStatus();
        LocalDateTime active = null;
        GeocodingConfig geo = new GeocodingConfig();

        // Raise popup for confirmation( logic is available in frontend for that )
        if(activeStatus == true){

            active = LocalDateTime.now();

            GeocodingConfig config = configRepository.findByActiveStatus(true);

            if(config != null){
                config.setActiveStatus(false);
                config.setDeactivateAt(LocalDateTime.now());
                configRepository.save(config);
            }

            geo.setActiveAt(LocalDateTime.now());
        }

        geo.setActiveStatus(geocodingConfig.isActiveStatus());
        geo.setApiProvider(geocodingConfig.getApiProvider());
        geo.setApiKey(geocodingConfig.getApiKey());
        geo.setUsers(user);

        configRepository.save(geo);

    }

    public Optional<GeocodingConfig> getLatestConfig() {
        return Optional.ofNullable(configRepository.findTopByOrderByCreatedAtDesc());
    }

    public String changeGeoCodingStatus(Long id) {

        GeocodingConfig geocodingConfig = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Geocoding config not found for ID: " + id));

        boolean currentStatus = geocodingConfig.isActiveStatus();
        System.out.println("Current status : " + currentStatus);

        if (currentStatus) {
            // This config is already active, so deactivate it
            geocodingConfig.setActiveStatus(false);
            geocodingConfig.setDeactivateAt(LocalDateTime.now());
        } else {
            // Make this config active and deactivate any other active one
            GeocodingConfig activeConfig = configRepository.findByActiveStatus(true);
            if (activeConfig != null && !activeConfig.getId().equals(geocodingConfig.getId())) {
                activeConfig.setActiveStatus(false);
                activeConfig.setDeactivateAt(LocalDateTime.now());
                configRepository.save(activeConfig);
            }

            geocodingConfig.setActiveStatus(true);
            geocodingConfig.setActiveAt(LocalDateTime.now());
        }

        configRepository.save(geocodingConfig);

        return "Status of ID " + geocodingConfig.getId() + " changed successfully to " +
                (geocodingConfig.isActiveStatus() ? "ACTIVE" : "INACTIVE");
    }


    public List<GeocodingConfig> getAllConfigs() {
        return configRepository.findAllByOrderByCreatedAtDesc();
    }

    public String getCurrentProvider() {
        GeocodingConfig geocodingConfig = configRepository. findByActiveStatus(true);
        return geocodingConfig.getApiProvider();
    }

    public String getCurrentApiKey() {
        GeocodingConfig geocodingConfig = configRepository. findByActiveStatus(true);
        return geocodingConfig.getApiKey();
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

        CurrentStatus status = revenueSettingRequestDTO.getCurrentStatus();
        LocalDateTime active = null;

        // Raise popup for confirmation( logic is available in frontend for that )
        if(status.equals(CurrentStatus.ACTIVE)){

            active = LocalDateTime.now();

            RevenueBreakDown breakDown = revenueBreakDownRepository.findByCurrentStatus(CurrentStatus.ACTIVE);

            if(breakDown != null){
                breakDown.setCurrentStatus(CurrentStatus.INACTIVE);
                breakDown.setDeactivateAt(LocalDateTime.now());
                revenueBreakDownRepository.save(breakDown);
            }

        }

        RevenueBreakDown revenueBreakDown = RevenueBreakDown.builder()
                .deliveryAgent(revenueSettingRequestDTO.getDeliveryAgentRevenue())
                .serviceProvider(revenueSettingRequestDTO.getServiceProviderRevenue())
                .currentStatus(revenueSettingRequestDTO.getCurrentStatus())
                .build();

        revenueBreakDownRepository.save(revenueBreakDown);

        return new RevenueSettingResponseDTO(revenueSettingRequestDTO.getServiceProviderRevenue(),
                revenueSettingRequestDTO.getDeliveryAgentRevenue(),
                "Revenue is set as Delivery Agent : " + revenueSettingRequestDTO.getDeliveryAgentRevenue()
                        + "% Service Provider : " + revenueSettingRequestDTO.getServiceProviderRevenue() + "%");

    }

    public List<RevenueBreakDown> getRevenue() {
        List<RevenueBreakDown> revenueBreakDowns = revenueBreakDownRepository.findAll();
        return revenueBreakDowns;
    }

    public String changeRevenueBreakdownStatus(Long id) {
        RevenueBreakDown revenueBreakDown = revenueBreakDownRepository.findById(id).orElse(null);

        if(revenueBreakDown.getCurrentStatus() == CurrentStatus.INACTIVE){
            revenueBreakDown.setCurrentStatus(CurrentStatus.ACTIVE);
            revenueBreakDown.setActiveAt(LocalDateTime.now());
        } else {
            revenueBreakDown.setCurrentStatus(CurrentStatus.INACTIVE);
            revenueBreakDown.setDeactivateAt(LocalDateTime.now());
        }

        revenueBreakDownRepository.save(revenueBreakDown);

        return "Status of " + revenueBreakDown.getRevenueId() + "is changed successfully.";
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
