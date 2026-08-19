package com.ecorides.payload.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private Long id;

    @NotNull(message = "Booking is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    private String invoiceNumber;

    private BigDecimal amount;

    private BigDecimal tax;

    private BigDecimal totalAmount;

    private LocalDateTime generatedAt;

    private LocalDateTime updatedAt;
}