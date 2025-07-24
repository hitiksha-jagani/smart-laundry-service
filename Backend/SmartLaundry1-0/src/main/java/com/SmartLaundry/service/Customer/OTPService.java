package com.SmartLaundry.service.Customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SecureRandom random = new SecureRandom();
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    private String generateOtpKey(String identifier) {
        return "OTP::" + identifier.toLowerCase().trim();
    }

    public String generateOtp(String identifier) {
        String key = generateOtpKey(identifier);
        String otp = String.format("%06d", random.nextInt(1000000));
        redisTemplate.opsForValue().set(key, otp, OTP_TTL);
        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        String key = generateOtpKey(identifier);
        Object storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}

