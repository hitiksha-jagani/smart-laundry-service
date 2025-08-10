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
    private static final Duration COOLDOWN_DURATION = Duration.ofSeconds(60); // 60-second cooldown

    private String generateOtpKey(String identifier) {
        return "OTP::" + identifier.toLowerCase().trim();
    }

    private String generateTimestampKey(String identifier) {
        return "OTP_TIMESTAMP::" + identifier.toLowerCase().trim();
    }

    public boolean isInCooldown(String identifier) {
        String timestampKey = generateTimestampKey(identifier);
        Object lastSentObj = redisTemplate.opsForValue().get(timestampKey);
        if (lastSentObj instanceof Long lastSentTime) {
            long now = System.currentTimeMillis();
            return (now - lastSentTime) < COOLDOWN_DURATION.toMillis();
        }
        return false;
    }

    public long remainingCooldownMillis(String identifier) {
        String timestampKey = generateTimestampKey(identifier);
        Object lastSentObj = redisTemplate.opsForValue().get(timestampKey);
        if (lastSentObj instanceof Long lastSentTime) {
            long now = System.currentTimeMillis();
            long diff = now - lastSentTime;
            return Math.max(0, COOLDOWN_DURATION.toMillis() - diff);
        }
        return 0;
    }

    public String generateOtp(String identifier) {
        String key = generateOtpKey(identifier);
        String timestampKey = generateTimestampKey(identifier);

        String otp = String.format("%06d", random.nextInt(1000000));

        redisTemplate.opsForValue().set(key, otp, OTP_TTL);
        redisTemplate.opsForValue().set(timestampKey, System.currentTimeMillis(), OTP_TTL); // expire with OTP

        return otp;
    }

    public boolean validateOtp(String identifier, String otp) {
        String key = generateOtpKey(identifier);
        Object storedOtp = redisTemplate.opsForValue().get(key);
        if (storedOtp != null && storedOtp.equals(otp)) {
            redisTemplate.delete(key);
            redisTemplate.delete(generateTimestampKey(identifier)); // remove cooldown as well
            return true;
        }
        return false;
    }
}



