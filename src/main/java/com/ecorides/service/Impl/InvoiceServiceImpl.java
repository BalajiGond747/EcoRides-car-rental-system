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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

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

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.ACTIVE && booking.getStatus() != BookingStatus.COMPLETED) {

            throw new BadRequestException("Invoice can only be generated for a confirmed booking");
        }

        Optional<Invoice> existing = invoiceRepository.findByBookingId(bookingId);

        if (existing.isPresent()) {
            return InvoiceMapper.toDto(existing.get());
        }

        BigDecimal amount = booking.getRentalAmount();

        BigDecimal tax = booking.getTaxAmount();

        BigDecimal total = booking.getTotalAmount();

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

        Booking booking = invoice.getBooking();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf);

            document.setMargins(36, 36, 36, 36);

            Table headerTable = new Table(new float[]{70, 30});

            headerTable.setWidth(UnitValue.createPercentValue(100));

            Cell companyCell = new Cell().setBorder(Border.NO_BORDER);

            companyCell.add(new Paragraph("ECORIDES").setBold()
                    .setFontSize(24)
                    .setFontColor(new DeviceRgb(5, 150, 105)));

            companyCell.add(new Paragraph("Electric Car Rental").setFontSize(10)
                    .setFontColor(new DeviceRgb(100, 100, 100)));

            headerTable.addCell(companyCell);

            Cell invoiceTitleCell = new Cell().setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            invoiceTitleCell.add(new Paragraph("RENTAL INVOICE").setBold()
                    .setFontSize(16));

            invoiceTitleCell.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()).setFontSize(9));

            headerTable.addCell(invoiceTitleCell);

            document.add(headerTable);

            SolidLine line = new SolidLine(1f);
            line.setColor(new DeviceRgb(5, 150, 105));

            document.add(new LineSeparator(line));

            Table invoiceInfo = new Table(new float[]{50, 50});

            invoiceInfo.setWidth(UnitValue.createPercentValue(100));

            invoiceInfo.setMarginTop(12);

            Cell invoiceDateCell = new Cell().setBorder(Border.NO_BORDER);

            invoiceDateCell.add(new Paragraph("Invoice Date").setBold()
                    .setFontSize(9));

            invoiceDateCell.add(new Paragraph(formatInvoiceDate(invoice.getGeneratedAt())).setFontSize(10));

            invoiceInfo.addCell(invoiceDateCell);

            Cell referenceCell = new Cell().setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            referenceCell.add(new Paragraph("Booking Reference").setBold()
                    .setFontSize(9));

            referenceCell.add(new Paragraph(booking.getBookingReference()).setFontSize(10));

            invoiceInfo.addCell(referenceCell);

            document.add(invoiceInfo);

            document.add(new Paragraph("CUSTOMER").setBold()
                    .setFontSize(11)
                    .setMarginTop(18)
                    .setMarginBottom(5));

            Table customerTable = new Table(new float[]{50, 50});

            customerTable.setWidth(UnitValue.createPercentValue(100));

            Cell customerNameCell = new Cell().setBackgroundColor(new DeviceRgb(248, 250, 252))
                    .setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1));

            customerNameCell.add(new Paragraph("Customer Name").setFontSize(8)
                    .setFontColor(new DeviceRgb(100, 100, 100)));

            customerNameCell.add(new Paragraph(booking.getUser()
                    .getFirstName() + " " + booking.getUser()
                    .getLastName()).setBold()
                    .setFontSize(10));

            customerTable.addCell(customerNameCell);

            Cell phoneCell = new Cell().setBackgroundColor(new DeviceRgb(248, 250, 252))
                    .setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1));

            phoneCell.add(new Paragraph("Phone").setFontSize(8)
                    .setFontColor(new DeviceRgb(100, 100, 100)));

            phoneCell.add(new Paragraph(booking.getUser()
                    .getPhone()).setBold()
                    .setFontSize(10));

            customerTable.addCell(phoneCell);

            document.add(customerTable);

            document.add(new Paragraph("RENTAL DETAILS").setBold()
                    .setFontSize(11)
                    .setMarginTop(18)
                    .setMarginBottom(5));

            Table rentalTable = new Table(new float[]{50, 50});

            rentalTable.setWidth(UnitValue.createPercentValue(100));

            addDetailCell(rentalTable, "Car", booking.getCar()
                    .getName());

            addDetailCell(rentalTable, "Location", booking.getLocation()
                    .getName());

            addDetailCell(rentalTable, "Start", formatInvoiceDateTime(booking.getStartTime()));

            addDetailCell(rentalTable, "End", formatInvoiceDateTime(booking.getEndTime()));

            long rentalDays = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartTime()
                    .toLocalDate(), booking.getEndTime()
                    .toLocalDate());

            rentalDays = Math.max(rentalDays, 1);

            addDetailCell(rentalTable, "Rental Duration", rentalDays + (rentalDays == 1 ? " Day" : " Days"));

            document.add(rentalTable);

            document.add(new Paragraph("BILLING SUMMARY").setBold()
                    .setFontSize(11)
                    .setMarginTop(18)
                    .setMarginBottom(5));

            Table billingTable = new Table(new float[]{70, 30});

            billingTable.setWidth(UnitValue.createPercentValue(100));

            Cell descriptionHeader = new Cell().setBackgroundColor(new DeviceRgb(5, 150, 105))
                    .setPadding(7);

            descriptionHeader.add(new Paragraph("DESCRIPTION").setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setFontSize(9));

            billingTable.addCell(descriptionHeader);

            Cell amountHeader = new Cell().setBackgroundColor(new DeviceRgb(5, 150, 105))
                    .setPadding(7)
                    .setTextAlignment(TextAlignment.RIGHT);

            amountHeader.add(new Paragraph("AMOUNT").setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setFontSize(9));

            billingTable.addCell(amountHeader);

            addBillingRow(billingTable, "Rental Charges", booking.getRentalAmount());

            if (booking.getDiscountAmount() != null && booking.getDiscountAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

                addBillingRow(billingTable, "Discount", booking.getDiscountAmount()
                        .negate());
            }

            if (booking.getTaxAmount() != null && booking.getTaxAmount()
                    .compareTo(BigDecimal.ZERO) > 0) {

                addBillingRow(billingTable, "GST", booking.getTaxAmount());
            }

            Cell totalLabelCell = new Cell().setBackgroundColor(new DeviceRgb(236, 253, 245))
                    .setPadding(9);

            totalLabelCell.add(new Paragraph("TOTAL PAID").setBold()
                    .setFontSize(11));

            billingTable.addCell(totalLabelCell);

            Cell totalAmountCell = new Cell().setBackgroundColor(new DeviceRgb(236, 253, 245))
                    .setPadding(9)
                    .setTextAlignment(TextAlignment.RIGHT);

            totalAmountCell.add(new Paragraph("₹" + formatMoney(booking.getTotalAmount())).setBold()
                    .setFontSize(13)
                    .setFontColor(new DeviceRgb(5, 150, 105)));

            billingTable.addCell(totalAmountCell);

            document.add(billingTable);

            Table paymentTable = new Table(new float[]{50, 50});

            paymentTable.setWidth(UnitValue.createPercentValue(100));

            paymentTable.setMarginTop(15);

            addDetailCell(paymentTable, "Payment Method", booking.getPayment() != null ? booking.getPayment()
                    .getPaymentMethod()
                    .toString() : "RAZORPAY");

            addDetailCell(paymentTable, "Payment Status", "PAID");

            document.add(paymentTable);

            document.add(new Paragraph("Thank you for choosing EcoRides.").setBold()
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(28));

            document.add(new Paragraph("This is a computer-generated invoice.").setFontSize(8)
                    .setFontColor(new DeviceRgb(120, 120, 120))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(4));

            document.close();

        } catch (Exception ex) {

            ex.printStackTrace();

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

    private void addDetailCell(Table table, String label, String value) {

        Cell cell = new Cell().setBackgroundColor(new DeviceRgb(248, 250, 252))
                .setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1))
                .setPadding(8);

        cell.add(new Paragraph(label).setFontSize(8)
                .setFontColor(new DeviceRgb(100, 100, 100)));

        cell.add(new Paragraph(value != null ? value : "—").setBold()
                .setFontSize(10));

        table.addCell(cell);
    }

    private void addBillingRow(Table table, String description, BigDecimal amount) {

        Cell descriptionCell = new Cell().setPadding(8);

        descriptionCell.add(new Paragraph(description).setFontSize(10));

        table.addCell(descriptionCell);

        Cell amountCell = new Cell().setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT);

        String prefix = amount.compareTo(BigDecimal.ZERO) < 0 ? "-₹" : "₹";

        amountCell.add(new Paragraph(prefix + formatMoney(amount.abs())).setFontSize(10));

        table.addCell(amountCell);
    }

    private String formatMoney(BigDecimal amount) {

        if (amount == null) {
            return "0.00";
        }

        return amount.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String formatInvoiceDate(LocalDateTime dateTime) {

        if (dateTime == null) {
            return "—";
        }

        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private String formatInvoiceDateTime(LocalDateTime dateTime) {

        if (dateTime == null) {
            return "—";
        }

        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }
}