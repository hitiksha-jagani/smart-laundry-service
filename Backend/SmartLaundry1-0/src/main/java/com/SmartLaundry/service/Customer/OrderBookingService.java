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
    //List<BookingItem> createBookingItemsFromRedis(String userId, String orderId);
    void saveInitialOrderDetails(String userId, BookOrderRequestDto dto);
    void saveSchedulePlan(String userId, SchedulePlanRequestDto dto);
    void saveContactInfo(String userId, ContactDetailsDto dto);
    OrderResponseDto createOrder(String userId);
}

