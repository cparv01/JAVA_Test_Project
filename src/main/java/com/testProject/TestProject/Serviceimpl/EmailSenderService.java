package com.testProject.TestProject.Serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("vohede5520@gmail.com"); // Your verified sender email
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content); // Use the provided content

            mailSender.send(message);

            System.out.println("Mail sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.");
        }
    }
}
