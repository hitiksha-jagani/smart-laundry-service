package com.SmartLaundry.repository;

import com.SmartLaundry.model.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.Order;
import java.util.List;

public interface BookingItemRepository  extends JpaRepository<BookingItem, String> {
    List<BookingItem> findAllByOrderByBookingItemIdAsc(); //BK00001
    List<BookingItem> findByOrder(Order order);
}
