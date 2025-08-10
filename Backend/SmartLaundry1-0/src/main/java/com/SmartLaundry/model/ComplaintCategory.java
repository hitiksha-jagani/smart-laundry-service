package com.SmartLaundry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complaint_category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long categoryId;

    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Category contains invalid characters.")
    @Column(name = "category_name", nullable = false)
    private String categoryName;
}
