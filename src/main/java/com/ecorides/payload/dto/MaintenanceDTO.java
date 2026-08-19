package com.ecorides.payload.dto;

import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.domain.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long id;

    @NotNull(message = "Car is required")
    @Positive(message = "Car ID must be positive")
    private Long carId;

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType type;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private MaintenanceStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}