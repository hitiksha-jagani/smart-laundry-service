package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.DayOfWeek;
import com.SmartLaundry.model.DeliveryAgent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AvailabilityDTO {
    private DayOfWeek dayOfWeek;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean holiday;
    private DeliveryAgent deliveryAgent;

    public AvailabilityDTO() {}

    public AvailabilityDTO(DayOfWeek dayOfWeek, LocalDate date, LocalTime startTime, LocalTime endTime, boolean holiday, DeliveryAgent deliveryAgent) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.holiday = holiday;
        this.deliveryAgent = deliveryAgent;
    }

}
