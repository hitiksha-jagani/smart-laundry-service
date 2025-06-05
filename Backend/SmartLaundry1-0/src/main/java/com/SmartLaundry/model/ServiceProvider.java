package com.SmartLaundry.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SERVICE_PROVIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProvider {

    @Id
    @GeneratedValue(generator = "service-provider-id-generator")
    @GenericGenerator(
            name = "service-provider-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "SP"),
                    @Parameter(name = "table_name", value = "SERVICE_PROVIDER"),  // match entity table name exactly
                    @Parameter(name = "column_name", value = "service_provider_id"),
                    @Parameter(name = "number_length", value = "4")
            }
    )
    @Column(name = "service_provider_id", nullable = false, updatable = false)
    private String serviceProviderId;

    @Column(name = "Business_Name", nullable = false)
    private String businessName;

    @Column(name = "Business_License_Number", nullable = true)
    private String businessLicenseNumber;

    @Column(name = "GST_Number", nullable = true)
    private String gstNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;


    @ElementCollection(targetClass = SchedulePlan.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "service_provider_schedule_plans",
            joinColumns = @JoinColumn(name = "service_provider_id")
    )
    @Column(name = "schedule_plan")
    @Schema(description = "List of schedule plans supported by the service provider.")
    private Set<SchedulePlan> schedulePlans = new HashSet<>();

    @Column(name = "Need_Of_Delivery_Agent", nullable = true)
    private Boolean needOfDeliveryAgent;

    @Lob
    @Column(name = "PAN_Card", nullable = true)
    private byte[] panCardImage;

    @Lob
    @Column(name = "Business_Utility_Bill", nullable = true)
    private byte[] businessUtilityBillImage;

    @Lob
    @Column(name = "Aadhar_Card", nullable = true)
    private byte[] aadharCardImage;

    @Lob
    @Column(name = "Photo", nullable = true)
    private byte[] photoImage;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Price> prices = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "Add_Id")
    private UserAddress address;

    @OneToOne
    @JoinColumn(name = "Bank_Account_Id")
    private BankAccount bankAccount;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<FeedbackProviders> feedbacks = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "SERVICE_PROVIDER_ITEMS",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Prevent unwanted recursion
    private List<Items> items = new ArrayList<>();


    // Getter for address, named consistently
    public UserAddress getAddress() {
        return address;
    }

    // Getter for photo image
    public byte[] getPhoto() {
        return this.photoImage;
    }

    public void setPhoto(byte[] photo) {
        this.photoImage = photo;
    }
}
