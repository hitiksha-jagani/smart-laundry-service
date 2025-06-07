package com.SmartLaundry.service.Customer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public void sendOtp(String phoneNumber, String otp) {
        Twilio.init(accountSid, authToken);

        String formattedPhoneNumber = "+" + phoneNumber;
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

        String formattedPhoneNumber = "+" + phoneNumber;

        Message.creator(
                new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                message
        ).create();

        System.out.println("Sent order notification to " + formattedPhoneNumber);
    }


}


