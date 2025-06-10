package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@Schema(description = "Represents a user with unique ID, phone and email.")
public class Users implements Serializable{

    @Getter
    @Id
    @GeneratedValue(generator = "user-id-generator")
    @GenericGenerator(
            name = "user-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "US"),
                    @Parameter(name = "table_name", value = "users"),
                    @Parameter(name = "column_name", value = "user_id"),
                    @Parameter(name = "number_length", value = "5")
            }
    )
    @Column(name = "user_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the users.", example = "US00001", accessMode = Schema.AccessMode.READ_ONLY)
    private String userId;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z\\\\s]+$", message = "First name contains invalid characters.")
    @Column(name = "first_name", nullable = false, unique = false, length = 100)
    @Schema(description = "The first name of the user.", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z\\\\s]+$", message = "Last name contains invalid characters.")
    @Column(name = "last_name", nullable = false, unique = false, length = 100)
    @Schema(description = "The last name of the user.", example = "Deo")
    private String lastName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(name = "phone_no", nullable = false, unique = true, length = 10)
    @Schema(description = "The phone number of the user.", example = "9867548934")
    private String phoneNo;

    @Column(name = "email", unique = true, length = 100)
    @Schema(description = "The email of the user.", example = "johndeo@gmail.com")
    private String email;

    @NotBlank(message = "Password is required.")
    @Column(name = "password", nullable = false)
    @Schema(description = "The password of the user.", example = "mypass@123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Schema(description = "Role of the user.", example = "ADMIN", accessMode = Schema.AccessMode.READ_ONLY)
    private UserRole role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp of the user creation.", example = "2025-05-21 00:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonManagedReference
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAddress address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();


}



