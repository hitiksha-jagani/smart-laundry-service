package com.SmartLaundry.model;

import com.twilio.rest.chat.v1.service.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "service_provider")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProvider implements Serializable {

    @Id
    @GeneratedValue(generator = "service-provider-id-generator")
    @GenericGenerator(
            name = "service-provider-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "SP"),
                    @Parameter(name = "table_name", value = "service_provider"),
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Builder.Default
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

    @Column(name = "PAN_Card", nullable = true)
    private String panCardImage;

    @Column(name = "Business_Utility_Bill", nullable = true)
    private String businessUtilityBillImage;

    @Column(name = "Aadhar_Card", nullable = true)
    private String aadharCardImage;

    @Column(name = "Photo", nullable = true)
    private String photoImage;

    @Builder.Default
    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Price> prices = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "Bank_Account_Id")
    private BankAccount bankAccount;

    @Builder.Default
    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<FeedbackProviders> feedbacks = new ArrayList<>();


    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Delivery agent is accepted or rejected.", example = "ACCEPTED")
    private Status status = Status.PENDING;

    public String getProviderId() {
        return serviceProviderId;
    }
}
