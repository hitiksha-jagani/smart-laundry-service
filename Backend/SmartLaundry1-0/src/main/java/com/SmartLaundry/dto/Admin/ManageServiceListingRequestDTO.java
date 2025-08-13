package com.SmartLaundry.dto.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManageServiceListingRequestDTO {

    @Size(min = 3, max = 100, message = "Item name must be between 3 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z+()\\s\\-]+$", message = "Service name contains invalid characters.")
    private String itemName;

    @Size(min = 3, max = 100, message = "Service name must be between 3 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z+\\s\\-_()]+$", message = "Service name contains invalid characters.")
    private String serviceName;

    private String subServiceName;
}
