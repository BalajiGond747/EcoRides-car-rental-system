package com.ecorides.repository;

import com.ecorides.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByBookingId(Long bookingId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);

}