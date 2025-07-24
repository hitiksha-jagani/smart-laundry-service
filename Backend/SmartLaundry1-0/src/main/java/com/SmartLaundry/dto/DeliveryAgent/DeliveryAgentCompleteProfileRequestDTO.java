package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.GENDER;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.Ignore;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Base64;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryAgentCompleteProfileRequestDTO implements Serializable {

    @NotNull(message = "Date of birth is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past(message = "Date of Birth must be in the past")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfBirth;

    @Size(min = 8, max = 10, message = "Vehicle number must be between 8 and 10 characters.")
    private String vehicleNumber;

    @NotBlank(message = "Bank name is required.")
    private String bankName;

    @NotBlank(message = "Account holder name is required.")
    private String accountHolderName;

    @NotBlank(message = "Bank account number is required.")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC Code is required.")
    private String ifscCode;

    @NotNull(message = "Gender is required.")
    private GENDER gender;

    @NotNull(message = "Aadhar card is required.")
    private MultipartFile aadharCardPhoto;

    private MultipartFile panCardPhoto;

    @NotNull(message = "Driving License is required.")
    private MultipartFile drivingLicensePhoto;

    @NotNull(message = "Profile photo is required.")
    private MultipartFile profilePhoto;

}
