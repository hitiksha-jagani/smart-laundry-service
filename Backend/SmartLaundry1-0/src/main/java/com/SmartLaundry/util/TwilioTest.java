//package com.SmartLaundry.util;
//
//import com.twilio.Twilio;
//import org.springframework.data.redis.connection.Message;
//
//public class TwilioTest {
//    public static void main(String[] args) {
//        Twilio.init("AC641161639a546dc1938a998fb19835ba", "8dc623a96318344a916dd6234615f4f");
//
//        Message message = Message.creator(
//                        new com.twilio.type.PhoneNumber("+919876543210"), // replace with your test number
//                        new com.twilio.type.PhoneNumber("+17077379501"),
//                        "Test Message from Twilio")
//                .create();
//
//        System.out.println("Message SID: " + message.getSid());
//    }
//}

