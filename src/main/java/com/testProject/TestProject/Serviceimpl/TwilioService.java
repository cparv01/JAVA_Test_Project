package com.testProject.TestProject.Serviceimpl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TwilioService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid == null || authToken == null || fromPhoneNumber == null) {
            throw new IllegalStateException("Twilio configuration is missing or invalid");
        }
        Twilio.init(accountSid, authToken);
    }

    public String sendSms(String toPhoneNumber, String messageContent) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    messageContent
            ).create();
            return message.getSid();
        } catch (Exception e) {
            System.out.println("Error sending SMS: {}");
            throw new RuntimeException("Failed to send SMS. Please try again later.");
        }
    }
}
