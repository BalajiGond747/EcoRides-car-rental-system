package com.ecorides.controller;

import com.ecorides.payload.dto.InvoiceDto;
import com.ecorides.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/{bookingId}")
    public InvoiceDto generate(@PathVariable Long bookingId) {
        return invoiceService.generateInvoice(bookingId);
    }

    @GetMapping("/booking/{bookingId}")
    public InvoiceDto getByBooking(@PathVariable Long bookingId) {
        return invoiceService.getInvoiceByBooking(bookingId);
    }
    @GetMapping("/{bookingId}/download")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long bookingId) {

        byte[] pdf = invoiceService.generateInvoicePdf(bookingId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice_" + bookingId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}