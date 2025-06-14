package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProviderAndStatus(ServiceProvider serviceProvider, OrderStatus status);

    // To list all orders sorted by OrderId in ascending order
    List<Order> findAllByOrderByOrderIdAsc();
    Optional<Order> findByorderId(String orderId);

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProvider_ServiceProviderIdAndStatus(String serviceProviderId, OrderStatus status);


    List<Order> findByPickupDate(LocalDate pickupDate);

    List<Order> findByStatusAndPickupDeliveryAgent(@Param("orderStatus") OrderStatus orderStatus,@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByPickupDeliveryAgent(@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByDeliveryDeliveryAgent(@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByStatusAndDeliveryDeliveryAgent(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByStatusAndDeliveryDeliveryAgentAndDeliveryDate(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent, @Param("now") LocalDate now);

    List<Order> findByStatusAndPickupDeliveryAgentAndPickupDate(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent, @Param("now") LocalDate now);



    Order findByOrderId(String orderId);

    List<Order> findByServiceProvider_ServiceProviderId(String providerId);
}