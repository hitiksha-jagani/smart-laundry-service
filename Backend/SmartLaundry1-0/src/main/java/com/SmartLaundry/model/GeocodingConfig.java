package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "geocoding_config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_provider", nullable = false)
    private String apiProvider; // e.g., "google", "opencage"

    @Column(name = "api_key", nullable = false, length = 500)
    private String apiKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Column(name = "active_status", nullable = false)
    private boolean activeStatus;

    @Column(name = "active_at")
    private LocalDateTime activeAt;

    @Column(name = "deactivate_at")
    private LocalDateTime deactivateAt;

    public GeocodingConfig(String apiProvider, String apiKey, String userId, boolean activeStatus) {
        this.apiProvider = apiProvider;
        this.apiKey = apiKey;
        this.createdAt = LocalDateTime.now();
        this.users = new Users();
        this.users.setUserId(userId);
        this.activeStatus = activeStatus;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }
}
