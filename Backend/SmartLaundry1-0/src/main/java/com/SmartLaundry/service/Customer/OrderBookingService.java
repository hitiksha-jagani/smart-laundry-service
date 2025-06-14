package com.SmartLaundry.service.Customer;
import com.SmartLaundry.dto.Customer.BookOrderRequestDto;
import com.SmartLaundry.dto.Customer.ContactDetailsDto;
import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.SchedulePlanRequestDto;
import com.SmartLaundry.model.BookingItem;
import com.SmartLaundry.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderBookingService {
    String saveInitialOrderDetails(String userId, BookOrderRequestDto dto);
    void saveSchedulePlan(String userId, String dummyOrderId, SchedulePlanRequestDto dto);
    void saveContactInfo(String userId, String dummyOrderId, ContactDetailsDto dto);
    OrderResponseDto createOrder(String userId, String dummyOrderId);
}


