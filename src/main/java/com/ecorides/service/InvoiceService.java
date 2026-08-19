package com.ecorides.service;


import com.ecorides.payload.dto.InvoiceDTO;

public interface InvoiceService {

    InvoiceDTO generateInvoice(Long bookingId);

    InvoiceDTO getInvoiceByBooking(Long bookingId);

    byte[] generateInvoicePdf(Long bookingId);
}