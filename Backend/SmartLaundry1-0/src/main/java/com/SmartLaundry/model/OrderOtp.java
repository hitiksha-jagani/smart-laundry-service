package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Order_OTP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"order", "user", "agent"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Otp_Id")
    private Integer otpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Order_Id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "Agent_Id", nullable = true)
    private DeliveryAgent agent;

    @Column(name = "Otp_Code", nullable = false, length = 10)
    private String otpCode;

    @Column(name = "Generated_At", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "Expires_At", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "Is_Used", nullable = false)
    private Boolean isUsed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    private OtpPurpose purpose;
}
