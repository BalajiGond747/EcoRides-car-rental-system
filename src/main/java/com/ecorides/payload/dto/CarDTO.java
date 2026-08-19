package com.ecorides.payload.dto;

import com.ecorides.domain.CarStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {

    private Long id;

    @NotBlank(message = "Car name required")
    private String name;

    @NotBlank(message = "Category required")
    private String category;

    @NotBlank(message = "Registration number required")
    private String registrationNumber;

    @NotNull(message = "Price per day required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerDay;

    @NotNull(message = "Battery level required")
    @Min(value = 0, message = "Battery cannot be less than 0")
    @Max(value = 100, message = "Battery cannot exceed 100")
    private Integer batteryLevel;

    @NotNull(message = "Range required")
    @Positive(message = "Range must be positive")
    private Integer rangeKm;
    
    private CarStatus status;

    @NotNull(message = "Location required")
    private Long locationId;

    @Positive(message = "Seating capacity must be positive")
    private Integer seatingCapacity;

    private String imageUrl;

    private Boolean isActive;
}