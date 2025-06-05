package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.GENDER;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAgentCompleteProfileRequestDTO {

    @NotNull(message = "Date of birth is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past(message = "Date of Birth must be in the past")
    private Date dateOfBirth;

    @Size(min = 8, max = 10, message = "Vehicle number must be between 8 and 10 characters.")
    private String vehicleNumber;

    @NotNull(message = "aadhar card is required.")
    private byte[] aadharCardPhoto;

    private byte[] panCardPhoto;

    @NotNull(message = "Driving License is required.")
    private byte[] drivingLicensePhoto;

    @NotBlank(message = "Bank name is required.")
    private String bankName;

    @NotBlank(message = "Account holder name is required.")
    private String accountHolderName;

    @NotBlank(message = "Bank account number is required.")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC Code is required.")
    private String ifscCode;

    @NotNull(message = "Profile photo is required.")
    private byte[] profilePhoto;

    @NotNull(message = "Gender is required.")
    private GENDER gender;
}
