package com.SmartLaundry.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
                    @Parameter(name = "table_name", value = "service_providers"),
                    @Parameter(name = "column_name", value = "service_provider_id"),
                    @Parameter(name = "number_length", value = "4")
            }
    )
    @Column(name = "service_provider_id", nullable = false, updatable = false)
    private String service_provider_Id;

    @Column(name = "Business_Name", nullable = false)
    private String businessName;

    @Column(name = "Business_License_Number")
    private String businessLicenseNumber;

    @Column(name = "GST_Number")
    private String gstNumber;

    @Column(name = "Schedule_Plan")
    private String schedulePlan;

    @Column(name = "Need_Of_Delivery_Agent")
    private Boolean needOfDeliveryAgent;

    @Lob
    @Column(name = "PAN_Card")
    private byte[] panCardImage;

    @Lob
    @Column(name = "Business_Utility_Bill")
    private byte[] businessUtilityBillImage;

    @Lob
    @Column(name = "Aadhar_Card")
    private byte[] aadharCardImage;

    @Lob
    @Column(name = "Photo")
    private byte[] photoImage;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Price> prices = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "Add_Id")
    private UserAddress address;

    @ManyToOne
    @JoinColumn(name = "Bank_Account_Id")
    private BankAccount bankAccount;

    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeedbackProviders> feedbacks;

    @ManyToMany
    @JoinTable(
            name = "provider_service",
            joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Services> services = new HashSet<>();

    public UserAddress getUserAddress() {
        return  address;
    }

    public String getServiceProviderId() {
        return this.service_provider_Id;
    }

    public byte[] getPhoto() {
        return this.photoImage;
    }
}
