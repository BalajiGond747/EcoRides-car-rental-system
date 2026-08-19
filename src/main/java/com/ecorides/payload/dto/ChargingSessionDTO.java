package com.ecorides.payload.dto;

import com.ecorides.domain.ChargingStatus;
import jakarta.validation.constraints.DecimalMin;
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
public class ChargingSessionDTO {

    private Long id;

    @NotNull(message = "Car is required")
    @Positive(message = "Car ID must be positive")
    private Long carId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "Charge added is required")
    @DecimalMin(value = "0.01", message = "Charge added must be greater than 0")
    private BigDecimal chargeAdded;

    private ChargingStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}