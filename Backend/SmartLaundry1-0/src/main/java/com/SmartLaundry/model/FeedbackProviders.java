package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.SmartLaundry.model.Users;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "FEEDBACK_PROVIDERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FeedbackProviders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Feedback_Id")
    private Long feedbackId;


    @ManyToOne(fetch = FetchType.EAGER) // Ensure this is present
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    public Users getUser() {
        return user;
    }


    @Column(name = "Rating", nullable = false)
    private Integer rating;

    @Column(name = "Review", length = 1000)
    private String review;

    @Column(name = "Response", length = 1000)
    private String response;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    public String getFirstName()
    {
        return user.getFirstName();
    }
    public String getLastName() {return user.getLastName();}
}
