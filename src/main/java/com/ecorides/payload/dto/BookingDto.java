package com.ecorides.payload.dto;

import com.ecorides.domain.BookingStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    private Long userId;

    @NotNull(message = "Car is required")
    @Positive(message = "Car ID must be positive")
    private Long carId;

    @NotNull(message = "Location is required")
    @Positive(message = "Location ID must be positive")
    private Long locationId;

    private String couponCode;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    private BigDecimal totalAmount;

    private BookingStatus status;

    private String bookingReference;

    private String cancellationReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}