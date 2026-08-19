package com.ecorides.service.Impl;

import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.Booking;
import com.ecorides.entity.Invoice;
import com.ecorides.entity.Payment;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.InvoiceException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.mappers.InvoiceMapper;
import com.ecorides.payload.dto.InvoiceDTO;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.InvoiceRepository;
import com.ecorides.repository.PaymentRepository;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.InvoiceService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    public InvoiceDTO generateInvoice(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment incomplete");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Booking not completed");
        }

        if (invoiceRepository.findByBookingId(bookingId)
                .isPresent()) {
            throw new BadRequestException("Invoice already exists");
        }

        BigDecimal amount = booking.getTotalAmount();

        BigDecimal tax = amount.multiply(GST_RATE);

        BigDecimal total = amount.add(tax);

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
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByBooking(Long bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !invoice.getBooking()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to access this invoice");
        }

        return InvoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long bookingId) {

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !invoice.getBooking()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to download this invoice");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("EcoRides Invoice"));
            document.add(new Paragraph("Invoice Number : " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Booking ID : " + invoice.getBooking()
                    .getId()));
            document.add(new Paragraph("Amount : ₹" + invoice.getAmount()));
            document.add(new Paragraph("GST : ₹" + invoice.getTax()));
            document.add(new Paragraph("Total : ₹" + invoice.getTotalAmount()));
            document.close();

        } catch (Exception ex) {
            throw new InvoiceException("PDF generation failed");
        }

        return out.toByteArray();

    }

    private String generateInvoiceNumber() {

        return "INV-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName()
                .isBlank()) {

            throw new AccessDeniedException("Authenticated user not found");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

}