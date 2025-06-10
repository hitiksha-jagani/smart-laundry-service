package com.SmartLaundry.repository;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    // To list all orders sorted by OrderId in ascending order
    List<Order> findAllByOrderByOrderIdAsc();

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProvider_User_UserIdAndStatus(String serviceProviderId, OrderStatus status);


    @Query("SELECT o FROM Order o WHERE o.serviceProvider.user.userId = :providerId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrderHistoryByProviderAndStatus(@Param("providerId") String providerId,
                                                    @Param("status") OrderStatus status);

}
