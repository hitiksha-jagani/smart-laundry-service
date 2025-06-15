package com.SmartLaundry.service.Customer;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    private String accountSid;
    private String authToken;
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        this.accountSid = System.getProperty("TWILIO_ACCOUNT_SID");
        this.authToken = System.getProperty("TWILIO_AUTH_TOKEN");
        this.twilioPhoneNumber = System.getProperty("TWILIO_PHONE_NUMBER");

        System.out.println("Twilio Account SID: " + accountSid);
        System.out.println("Twilio Auth Token: " + authToken);
        System.out.println("Twilio Phone Number: " + twilioPhoneNumber);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number is null");
        }

        String digitsOnly = phoneNumber.replaceAll("\\D", "");

        if (digitsOnly.length() == 10) {
            return "+91" + digitsOnly;
        } else if (digitsOnly.length() == 12 && digitsOnly.startsWith("91")) {
            return "+" + digitsOnly;
        } else if (digitsOnly.length() == 13 && digitsOnly.startsWith("91")) {
            return "+" + digitsOnly.substring(1); // remove double +
        } else if (digitsOnly.startsWith("0") && digitsOnly.length() == 11) {
            return "+91" + digitsOnly.substring(1);
        } else {
            throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
        }
    }

    public void sendOtp(String phoneNumber, String otp) {
        Twilio.init(accountSid, authToken);

        String formattedPhoneNumber = normalizePhoneNumber(phoneNumber);
        String messageBody = "Your OTP code is: " + otp;

        Message.creator(
                new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();

        System.out.println("Sent OTP " + otp + " to " + formattedPhoneNumber);
    }

    public void sendOrderStatusNotification(String phoneNumber, String message) {
        Twilio.init(accountSid, authToken);

        String formattedPhoneNumber = normalizePhoneNumber(phoneNumber);

        Message.creator(
                new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                message
        ).create();

        System.out.println("Sent order notification to " + formattedPhoneNumber);
    }

    public void sendSms(String phoneNumber, String messageBody) {
        try {
            Twilio.init(accountSid, authToken);
            String formattedPhoneNumber = normalizePhoneNumber(phoneNumber);

            Message.creator(
                    new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                    new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                    messageBody
            ).create();

            System.out.println("Sent SMS to " + formattedPhoneNumber);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + phoneNumber + ": " + e.getMessage());
        }
    }
}
