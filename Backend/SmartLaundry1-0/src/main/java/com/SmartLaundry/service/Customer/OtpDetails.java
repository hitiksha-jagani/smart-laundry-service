package com.SmartLaundry.service;
import java.time.LocalDateTime;

public class OtpDetails {
    private String otp;
    private LocalDateTime expiryTime;

    public OtpDetails(String otp, int validityMinutes) {
        this.otp = otp;
        this.expiryTime = LocalDateTime.now().plusMinutes(validityMinutes);
    }

    public String getOtp() {
        return otp;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
