package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
@ToString(exclude = "orderSchedulePlan")
@Schema(description = "Represents an order placed by a customer to a service provider.")
public class Order implements Serializable {

    @Id
    @GeneratedValue(generator = "order-id-generator")
    @GenericGenerator(
            name = "order-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "ODR"),
                    @Parameter(name = "table_name", value = "orders"),
                    @Parameter(name = "column_name", value = "order_id"),
                    @Parameter(name = "number_length", value = "5")
            }
    )
    @Column(name = "order_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the order.", example = "ODR00001", accessMode = Schema.AccessMode.READ_ONLY)
    private String orderId;

    private Double latitude;
    private Double longitude;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Reference to the customer placing the order.")
    private Users users;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "service_provider_id", nullable = false)
    @Schema(description = "Reference to the service provider assigned to the order.")
    private ServiceProvider serviceProvider;

    @NotNull(message = "Pickup date is required.")
    @Column(name = "pickup_date", nullable = false)
    @Schema(description = "Date of pickup for the order.", example = "2025-06-01")
    private LocalDate pickupDate;

    @NotNull(message = "Pickup time is required.")
    @Column(name = "pickup_time", nullable = false)
    @Schema(description = "Time of pickup for the order.", example = "10:30:00")
    private LocalTime pickupTime;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference
    private OrderSchedulePlan orderSchedulePlan;

    @NotBlank(message = "Contact name is required.")
    @Size(max = 100, message = "Contact name must not exceed 100 characters.")
    @Column(name = "contact_name", nullable = false, length = 100)
    @Schema(description = "Name of the person to contact at pickup location.", example = "Ravi Kumar")
    private String contactName;

    @NotBlank(message = "Contact phone is required.")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Contact phone must be a 10-12 digit number, optionally starting with '+'.")
    @Column(name = "contact_phone", nullable = false, length = 15)
    @Schema(description = "Phone number for contact at pickup location.", example = "919876543210")
    private String contactPhone;

    @NotBlank(message = "Contact address is required.")
    @Size(max = 255, message = "Contact address must not exceed 255 characters.")
    @Column(name = "contact_address", nullable = false)
    @Schema(description = "Pickup address for the order.", example = "123 MG Road, Bengaluru")
    private String contactAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Current status of the order.", example = "PENDING")
    private OrderStatus status = OrderStatus.PENDING;

    @JsonBackReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BookingItem> bookingItems;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "promo_code", nullable = true)
    @Schema(description = "Promotion id.")
    private Promotion promotion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp of when the order was created.", example = "2025-05-27T10:15:30")
    private LocalDateTime createdAt;

    @Column(name = "delivery_date", nullable = true)
    @Schema(description = "Date of delivery for the order.", example = "2025-06-01")
    private LocalDate deliveryDate;

    @Column(name = "delivery_time")
    @Schema(description = "Time of delivery for the order.", example = "10:30:00")
    private LocalTime deliveryTime;

    @Column(name = "total_km")
    private Double totalKm;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pickup_delivery_agent_id")
    private DeliveryAgent pickupDeliveryAgent;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "delivery_delivery_agent_id")
    private DeliveryAgent deliveryDeliveryAgent;

    public Users getUser() {
        return users;
    }

    @Builder
    public Order(Double latitude, Double longitude, Users users, ServiceProvider serviceProvider,
                 LocalDate pickupDate, LocalTime pickupTime, String contactName, String contactPhone,
                 String contactAddress, OrderStatus status, LocalDateTime createdAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.users = users;
        this.serviceProvider = serviceProvider;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactAddress = contactAddress;
        this.status = status;
        this.createdAt = createdAt;
    }
}
