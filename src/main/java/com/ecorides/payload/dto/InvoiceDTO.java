package com.ecorides.payload.dto;

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

    private Long bookingId;

    private String invoiceNumber;

    private BigDecimal amount;

    private BigDecimal tax;

    private BigDecimal totalAmount;

    private LocalDateTime generatedAt;

    private LocalDateTime updatedAt;
}