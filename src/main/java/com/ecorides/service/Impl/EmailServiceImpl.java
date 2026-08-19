package com.ecorides.service.Impl;

import com.ecorides.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("EcoRides OTP Verification");
        mail.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");
        mailSender.send(mail);
    }
}