package com.carrent.carrental.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.InvoiceDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Invoice;
import com.carrent.carrental.mappers.InvoiceMapper;
import com.carrent.carrental.repository.BookingRepository;
import com.carrent.carrental.repository.InvoiceRepository;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,BookingRepository bookingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository=bookingRepository;
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
    // Load the booking from repository
    Booking booking = bookingRepository.findById(invoiceDTO.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    Invoice invoice = InvoiceMapper.toEntity(invoiceDTO, booking);
    Invoice savedInvoice = invoiceRepository.save(invoice);
    return InvoiceMapper.toDTO(savedInvoice);
}

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(InvoiceMapper::toDTO).toList();
    }

    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return InvoiceMapper.toDTO(invoice);
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        existingInvoice.setGeneratedAt(invoiceDTO.getGeneratedAt());
        existingInvoice.setAmount(invoiceDTO.getAmount());
        existingInvoice.setPdfUrl(invoiceDTO.getPdfUrl());

        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        return InvoiceMapper.toDTO(updatedInvoice);
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id))
            throw new RuntimeException("Invoice not found");
        invoiceRepository.deleteById(id);
    }
}

