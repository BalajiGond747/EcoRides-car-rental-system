package com.ecorides.service.Impl;

import com.ecorides.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final RestClient restClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public EmailServiceImpl(@Value("${brevo.api-key:}") String apiKey, @Value("${brevo.sender-email:}") String senderEmail, @Value("${brevo.sender-name:EcoRides}") String senderName) {

        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    @Override
    public void sendOtpEmail(String email, String otp) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Brevo API key is missing");
        }

        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalStateException("Brevo sender email is missing");
        }

        Map<String, Object> requestBody = Map.of("sender", Map.of("name", senderName, "email", senderEmail), "to", List.of(Map.of("email", email)), "subject", "EcoRides OTP Verification", "textContent", "Hello,\n\n" + "Your EcoRides verification code is: " + otp + "\n\nThis OTP is valid for 5 minutes." + "\n\nIf you did not request this code, " + "please ignore this email.");

        restClient.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
