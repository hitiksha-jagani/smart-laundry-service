package com.SmartLaundry.controller.Customer;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.OTPService;
import com.SmartLaundry.service.Customer.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @RequestMapping(value = "/send/sms", method = {RequestMethod.GET, RequestMethod.POST})
    public String sendOtpSms(@RequestParam String phone) {
        String otp = otpService.generateOtp(phone);
        smsService.sendOtp(phone, otp);
        return "OTP sent to your phone. Please check your phone.";
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String key, @RequestParam String otp) {
        return otpService.validateOtp(key, otp) ? "OTP Verified Successfully" : "Invalid OTP";
    }

    @PostMapping("/send/email")
    public String sendOtpEmail(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);
        return "OTP sent to your email. Please check your email.";
    }
}
