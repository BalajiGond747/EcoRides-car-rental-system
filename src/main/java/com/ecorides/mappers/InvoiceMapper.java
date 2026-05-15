package com.ecorides.mappers;

import com.ecorides.entity.Invoice;
import com.ecorides.payload.dto.InvoiceDto;

public class InvoiceMapper {

    private InvoiceMapper() {}

    public static InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) return null;

        return InvoiceDto.builder()
                .id(invoice.getId())
                .bookingId(invoice.getBooking().getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .amount(invoice.getAmount())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .generatedAt(invoice.getGeneratedAt().toString())
                .build();
    }
}