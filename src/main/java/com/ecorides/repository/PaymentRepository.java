package com.ecorides.repository;

import com.ecorides.domain.PaymentMethod;
import com.ecorides.domain.PaymentStatus;
import com.ecorides.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByRazorpayOrderId(String orderId);

    Optional<Payment> findByRazorpayPaymentId(String paymentId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    Page<Payment> findByStatusAndPaymentMethod(PaymentStatus status, PaymentMethod paymentMethod, Pageable pageable);

    @Query("""
            SELECT p FROM Payment p
            WHERE
                LOWER(p.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.razorpayPaymentId) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Payment> findBySearch(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT p FROM Payment p
            WHERE (
                LOWER(p.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.razorpayPaymentId) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND p.status = :status
            """)
    Page<Payment> findBySearchAndStatus(@Param("search") String search, @Param("status") PaymentStatus status, Pageable pageable);

    @Query("""
            SELECT p FROM Payment p
            WHERE (
                LOWER(p.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.razorpayPaymentId) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND p.paymentMethod = :paymentMethod
            """)
    Page<Payment> findBySearchAndPaymentMethod(@Param("search") String search, @Param("paymentMethod") PaymentMethod paymentMethod, Pageable pageable);

    @Query("""
            SELECT p FROM Payment p
            WHERE (
                LOWER(p.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.razorpayPaymentId) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND p.status = :status
            AND p.paymentMethod = :paymentMethod
            """)
    Page<Payment> findBySearchAndStatusAndPaymentMethod(@Param("search") String search, @Param("status") PaymentStatus status, @Param("paymentMethod") PaymentMethod paymentMethod, Pageable pageable);
}