package com.ecorides.payload.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {

    private Long id;

    private Long bookingId;

    private String invoiceNumber;

    private double amount;

    private double tax;

    private double totalAmount;

    private String generatedAt;
}