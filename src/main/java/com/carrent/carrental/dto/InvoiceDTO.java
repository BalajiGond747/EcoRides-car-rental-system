package com.carrent.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

    private Long id;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @PastOrPresent(message = "Generated date cannot be in the future")
    private LocalDateTime generatedAt;

    @Size(max = 255, message = "PDF URL must be less than 255 characters")
    private String pdfUrl; // optional
}
