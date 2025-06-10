package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.util.ImageUtil;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAgentImageDTO {
    private byte[] aadharCardPhoto;
    private byte[] panCardPhoto;
    private byte[] drivingLicensePhoto;
    private byte[] profilePhoto;

    // Add these for frontend display
    private String aadharCardPhotoBase64;
    private String panCardPhotoBase64;
    private String drivingLicensePhotoBase64;
    private String profilePhotoBase64;

    public void encodeBase64() {
        this.aadharCardPhotoBase64 = ImageUtil.toBase64(aadharCardPhoto);
        this.panCardPhotoBase64 = ImageUtil.toBase64(panCardPhoto);
        this.drivingLicensePhotoBase64 = ImageUtil.toBase64(drivingLicensePhoto);
        this.profilePhotoBase64 = ImageUtil.toBase64(profilePhoto);
    }
}

