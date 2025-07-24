package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.AvailabilityDTO;
import com.SmartLaundry.model.DayOfWeek;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.DeliveryAgentAvailability;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.AvailabilityRepository;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

//@author Hitiksha Jagani
@Service
public class AvailabilityService {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    // Save an availability
    @Transactional
    public String saveAvailability(String userId, List<AvailabilityDTO> dtoList) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        for(AvailabilityDTO availabilityDTO : dtoList){
            LocalDate targetDate = getDateForCurrentWeek(availabilityDTO.getDayOfWeek());

            System.out.println("Agent : " + agent);
            System.out.println("Date : " + targetDate);
            System.out.println("Saving: " + availabilityDTO.getDayOfWeek());

            DeliveryAgentAvailability deliveryAgentAvailability = DeliveryAgentAvailability.builder()
                            .deliveryAgent(agent)
                            .date(targetDate)
                            .startTime(availabilityDTO.getStartTime())
                            .endTime(availabilityDTO.getEndTime())
                            .holiday(availabilityDTO.isHoliday())
                            .dayOfWeek(availabilityDTO.getDayOfWeek())
                            .build();

            try {
                availabilityRepository.save(deliveryAgentAvailability);
            } catch (TransactionSystemException ex) {
                Throwable rootCause = ExceptionUtils.getRootCause(ex);
                log.error("Transaction failed due to: ", rootCause);
            }
        }

        return "Availability saved successfully.";
    }

    public String saveAvailabilityForNextWeek(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // 🟡 Fetch current week availability from DB
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        List<DeliveryAgentAvailability> currentWeekAvailabilities =
                availabilityRepository.findByDeliveryAgentAndDateBetween(agent, monday, sunday);

        for (DeliveryAgentAvailability availability : currentWeekAvailabilities) {
            LocalDate nextWeekDate = availability.getDate().plusWeeks(1);

            DeliveryAgentAvailability newAvailability = DeliveryAgentAvailability.builder()
                    .deliveryAgent(agent)
                    .date(nextWeekDate)
                    .startTime(availability.getStartTime())
                    .endTime(availability.getEndTime())
                    .holiday(availability.isHoliday())
                    .dayOfWeek(availability.getDayOfWeek())
                    .build();

            try {
                availabilityRepository.save(newAvailability);
            } catch (TransactionSystemException ex) {
                Throwable rootCause = ExceptionUtils.getRootCause(ex);
                log.error("Transaction failed due to: ", rootCause);
            }
        }

        return "Next week's availability saved successfully.";
    }

    private LocalDate getDateForNextWeek(String dayOfWeekStr) {
        java.time.DayOfWeek targetDay = java.time.DayOfWeek.valueOf(dayOfWeekStr.toUpperCase()); // MONDAY to SUNDAY
        LocalDate today = LocalDate.now();

        // Find the start of the current week (Monday)

        // Start of current week (Monday)
        LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        // Calculate the target day in the current week
        LocalDate currentWeekTargetDate = currentWeekStart.with(TemporalAdjusters.nextOrSame(targetDay));

        // Shift that day to next week
        return currentWeekTargetDate.plusWeeks(1);
    }

    public boolean isCurrentlyAvailable(String userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        com.SmartLaundry.model.DayOfWeek todayDay =
                com.SmartLaundry.model.DayOfWeek.valueOf(java.time.LocalDate.now().getDayOfWeek().name());
        System.out.println("Day of today : " + todayDay);

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsersUserId(userId);

        List<DeliveryAgentAvailability> availabilities = availabilityRepository
                .findByDeliveryAgentAndDayOfWeek(deliveryAgent, todayDay);

        for (DeliveryAgentAvailability availability : availabilities) {
            System.out.println("Start time : " + availability.getStartTime());
            System.out.println("End time : " + availability.getEndTime());

            if (!availability.isHoliday() &&
                    !now.isBefore(availability.getStartTime()) &&
                    !now.isAfter(availability.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    // Get saved availabilities for current week
    public List<DeliveryAgentAvailability> getThisWeeksAvailability(String userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return availabilityRepository.findByDeliveryAgentAndDateBetween(agent, startOfWeek, endOfWeek);
    }

    // Calculate exact date for current week based on enum
    private LocalDate getDateForCurrentWeek(DayOfWeek dayOfWeek) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        return monday.plusDays(dayOfWeek.ordinal());
    }

    // Delete availability
    public void deleteAvailability(String id) {
        availabilityRepository.deleteById(id);
    }

    // Modify availability
    public DeliveryAgentAvailability updateAvailability(String id, AvailabilityDTO dto) {
        DeliveryAgentAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setHoliday(dto.isHoliday());

        return availabilityRepository.save(availability);
    }
}
