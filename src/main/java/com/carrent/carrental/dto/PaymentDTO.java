package com.carrent.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long id;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @PastOrPresent(message = "Payment date cannot be in the future")
    private LocalDateTime paymentDate;

    @NotBlank(message = "Status is required")
    private String status; // Better to validate against enum in service

    @NotBlank(message = "Payment method is required")
    @Size(max = 30, message = "Payment method must be less than 30 characters")
    private String paymentMethod;

    private String transactionId; // optional → no validation
}
