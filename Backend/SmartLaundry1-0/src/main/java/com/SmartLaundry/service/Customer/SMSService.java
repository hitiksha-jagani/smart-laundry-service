package com.SmartLaundry.service.Customer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
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
    public void sendOtp(String phoneNumber, String otp) {
        Twilio.init(accountSid, authToken);

        String formattedPhoneNumber = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
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


