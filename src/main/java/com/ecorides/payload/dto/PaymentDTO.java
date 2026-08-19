package com.ecorides.payload.dto;

import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
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
public class PaymentDTO {

    private Long id;

    @NotNull(message = "Booking is required")
    @Positive(message = "Booking ID must be positive")
    private Long bookingId;

    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private String key;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}