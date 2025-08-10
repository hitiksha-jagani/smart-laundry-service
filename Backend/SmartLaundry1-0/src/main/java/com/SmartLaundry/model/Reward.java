package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.twilio.rest.chat.v1.service.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "REWARDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reward_Id")
    private Long rewardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private Users user;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Points_Gained")
    private Integer pointsGained;

    @Column(name = "Points_Used")
    private Integer pointsUsed;
}

