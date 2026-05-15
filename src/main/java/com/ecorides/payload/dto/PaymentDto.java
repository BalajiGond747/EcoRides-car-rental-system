package com.ecorides.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

    private Long bookingId;

    private double amount;

    private String paymentMethod;


    private String razorpayOrderId;

    private String key;


    private String razorpayPaymentId;
    private String razorpaySignature;
}