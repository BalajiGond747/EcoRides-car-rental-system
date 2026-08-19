package com.ecorides.controller;

import com.ecorides.payload.dto.InvoiceDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> generateInvoice(@PathVariable Long bookingId) {

        InvoiceDTO invoice = invoiceService.generateInvoice(bookingId);

        return ResponseEntity.ok(ApiResponse.<InvoiceDTO>builder()
                .success(true)
                .message("Invoice generated successfully")
                .data(invoice)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceByBooking(@PathVariable Long bookingId) {

        InvoiceDTO invoice = invoiceService.getInvoiceByBooking(bookingId);

        return ResponseEntity.ok(ApiResponse.<InvoiceDTO>builder()
                .success(true)
                .message("Invoice fetched successfully")
                .data(invoice)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{bookingId}/download")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long bookingId) {

        byte[] pdf = invoiceService.generateInvoicePdf(bookingId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + bookingId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}