package com.ecorides.payload.dto;

import com.ecorides.domain.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;

    private Long userId;
    private Long carId;
    private Long locationId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double totalAmount;

    private BookingStatus status;

    private LocalDateTime createdAt;
    private String couponCode;
}