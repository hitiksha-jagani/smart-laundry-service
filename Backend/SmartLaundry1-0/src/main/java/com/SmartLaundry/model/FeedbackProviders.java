package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private Users users;

    public Users getUser() {
        return users;
    }


    @Column(name = "Rating", nullable = false)
    private Integer rating;

    @Column(name = "Review", length = 1000)
    private String review;

    @Column(name = "Response", length = 1000)
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Provider_Id", nullable = false)
    private ServiceProvider serviceProvider;

    public String getFirstName()
    {
        return users.getFirstName();
    }
}
