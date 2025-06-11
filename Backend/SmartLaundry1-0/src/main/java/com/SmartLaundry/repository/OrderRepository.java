package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    // To list all orders sorted by OrderId in ascending order
    List<Order> findAllByOrderByOrderIdAsc();

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProvider_ServiceProviderIdAndStatus(String serviceProviderId, OrderStatus status);

    List<Order> findByDeliveryAgent(DeliveryAgent deliveryAgent);

    List<Order> findByPickupDate(LocalDate pickupDate);

//    List<Order> findByServiceProvider_User_UserIdAndStatus(String serviceProviderId, OrderStatus orderStatus);

//    List<Order> findOrderHistoryByServiceProviderAndStatus(String serviceProviderId, OrderStatus status);
}