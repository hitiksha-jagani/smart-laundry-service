package com.SmartLaundry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "messages")
@Schema(description = "System-wide messages sent to different user types.")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "msg_id", nullable = false, updatable = false)
    @Schema(example = "1")
    private String msgId;

    @NotBlank(message = "Title is required.")
    @Column(name = "title", nullable = false)
    @Schema(description = "Message title", example = "System Maintenance")
    private String title;

    @NotBlank(message = "Description is required.")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Full message body", example = "The system will be under maintenance from 10 PM to 2 AM.")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    @Schema(description = "Target user type", example = "ALL")
    private UserType userType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Date and time when message was created", example = "2025-06-01T10:15:30")
    private LocalDateTime createdAt;

    @Column(name = "send_at")
    @Schema(description = "Scheduled time to send the message", example = "2025-06-01T12:00:00")
    private LocalDateTime sendAt;

    @PrePersist
    public void prePersist() {
        if (sendAt == null) {
            sendAt = LocalDateTime.now();
        }
    }

}
