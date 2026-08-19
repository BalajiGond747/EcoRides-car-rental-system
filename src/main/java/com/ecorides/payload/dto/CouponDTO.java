package com.ecorides.payload.dto;

import com.ecorides.domain.CouponType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code cannot exceed 50 characters")
    private String code;

    @NotNull(message = "Coupon type is required")
    private CouponType type;

    @NotNull(message = "Coupon value is required")
    @DecimalMin(value = "0.01", message = "Coupon value must be greater than 0")
    private BigDecimal value;

    @NotNull(message = "Expiry date is required")
    @FutureOrPresent(message = "Expiry date cannot be in the past")
    private LocalDate expiryDate;

    private Boolean isActive;
}