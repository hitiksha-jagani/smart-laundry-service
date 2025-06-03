package com.SmartLaundry.service;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new SecureRandom();
    private static final int OTP_VALIDITY_MINUTES = 5;

    public String generateOtp(String key) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(key, new OtpDetails(otp, OTP_VALIDITY_MINUTES));
        return otp;
    }

    public boolean validateOtp(String key, String otp) {
        OtpDetails otpDetails = otpStorage.get(key);
        if (otpDetails == null || otpDetails.isExpired()) {
            otpStorage.remove(key); // Clean up expired or used OTP
            return false;
        }

        boolean isValid = otpDetails.getOtp().equals(otp);
        if (isValid) {
            otpStorage.remove(key); // OTP is used, remove it
        }

        return isValid;
    }
}
