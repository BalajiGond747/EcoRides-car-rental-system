package com.ecorides.service;


import com.ecorides.payload.dto.InvoiceDto;

public interface InvoiceService {

    InvoiceDto generateInvoice(Long bookingId);

    InvoiceDto getInvoiceByBooking(Long bookingId);

    byte[] generateInvoicePdf(Long bookingId);
}