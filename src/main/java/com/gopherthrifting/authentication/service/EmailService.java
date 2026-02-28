package com.gopherthrifting.authentication.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Gopher Thrifting - Your Verification Code");
        message.setText("Your verification code is: " + otpCode
                + "\n\nThis code expires in 5 minutes."
                + "\n\nIf you did not request this code, please ignore this email.");
        mailSender.send(message);
    }
}
