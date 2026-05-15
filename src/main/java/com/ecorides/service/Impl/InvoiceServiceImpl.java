package com.ecorides.service.Impl;

import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.entity.Booking;
import com.ecorides.entity.Invoice;
import com.ecorides.entity.Payment;
import com.ecorides.exception.InvoiceException;
import com.ecorides.mappers.InvoiceMapper;
import com.ecorides.payload.dto.InvoiceDto;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.InvoiceRepository;
import com.ecorides.repository.PaymentRepository;
import com.ecorides.service.InvoiceService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceDto generateInvoice(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvoiceException("Booking not found"));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new InvoiceException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new InvoiceException("Cannot generate invoice before successful payment");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvoiceException("Invoice can only be generated after booking completion");
        }

        if (invoiceRepository.findByBookingId(bookingId).isPresent()) {
            throw new InvoiceException("Invoice already exists for this booking");
        }

        double amount = booking.getTotalAmount();
        double tax = amount * 0.18;
        double total = amount + tax;

        Invoice invoice = Invoice.builder()
                .booking(booking)
                .invoiceNumber(generateInvoiceNumber())
                .amount(amount)
                .tax(tax)
                .totalAmount(total)
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        return InvoiceMapper.toDto(saved);
    }

    @Override
    public InvoiceDto getInvoiceByBooking(Long bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new InvoiceException("Invoice not found"));

        return InvoiceMapper.toDto(invoice);
    }


    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public byte[] generateInvoicePdf(Long bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new InvoiceException("Invoice not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("EcoRides Invoice"));
            document.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Booking ID: " + invoice.getBooking().getId()));
            document.add(new Paragraph("Amount: ₹" + invoice.getAmount()));
            document.add(new Paragraph("Tax: ₹" + invoice.getTax()));
            document.add(new Paragraph("Total: ₹" + invoice.getTotalAmount()));

            document.close();

        } catch (Exception e) {
            throw new InvoiceException("Error generating PDF");
        }

        return out.toByteArray();
    }
}