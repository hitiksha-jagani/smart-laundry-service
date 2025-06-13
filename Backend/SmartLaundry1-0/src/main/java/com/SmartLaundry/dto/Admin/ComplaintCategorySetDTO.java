package com.SmartLaundry.dto.Admin;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintCategorySetDTO {
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Category contains invalid characters.")
    private String category;
}
