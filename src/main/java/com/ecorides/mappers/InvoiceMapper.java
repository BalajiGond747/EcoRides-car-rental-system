package com.ecorides.mappers;

import com.ecorides.entity.Invoice;
import com.ecorides.payload.dto.InvoiceDTO;

public class InvoiceMapper {

    public static InvoiceDTO toDto(Invoice invoice) {

        if (invoice == null) {
            return null;
        }

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .bookingId(invoice.getBooking() != null ? invoice.getBooking()
                        .getId() : null)
                .invoiceNumber(invoice.getInvoiceNumber())
                .amount(invoice.getAmount())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .generatedAt(invoice.getGeneratedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();

    }

}