package com.ecorides.repository;

import com.ecorides.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByBookingId(Long bookingId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}