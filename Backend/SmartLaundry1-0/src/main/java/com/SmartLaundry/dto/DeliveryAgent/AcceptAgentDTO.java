package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.GENDER;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptAgentDTO {
    private LocalDate dateOfBirth;
    private String vehicleNumber;
    private String bankName;
    private String accountHolderName;
    private String bankAccountNumber;
    private String ifscCode;
    private GENDER gender;

    private AcceptAgentDTO.DeliveryAgentImageDTO deliveryAgentImage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeliveryAgentImageDTO implements Serializable{

        private byte[] aadharCardPhoto;
        private byte[] panCardPhoto;
        private byte[] drivingLicensePhoto;
        private byte[] profilePhoto;
    }
}
