package com.carrent.carrental.mappers;

import java.time.LocalDateTime;

import com.carrent.carrental.dto.InvoiceDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Invoice;

public class InvoiceMapper {

    // Entity -> DTO
    public static InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setBookingId(invoice.getBooking() != null ? invoice.getBooking().getId() : null);
        dto.setAmount(invoice.getAmount());
        dto.setGeneratedAt(invoice.getGeneratedAt());
        dto.setPdfUrl(invoice.getPdfUrl());

        return dto;
    }

    // DTO -> Entity
    public static Invoice toEntity(InvoiceDTO dto, Booking booking) {
        if (dto == null) return null;

        Invoice invoice = new Invoice();
        invoice.setId(dto.getId());
        invoice.setBooking(booking); // pass Booking entity
        invoice.setAmount(dto.getAmount());
        invoice.setGeneratedAt(dto.getGeneratedAt() != null ? dto.getGeneratedAt() : LocalDateTime.now());
        invoice.setPdfUrl(dto.getPdfUrl());

        return invoice;
    }
}
