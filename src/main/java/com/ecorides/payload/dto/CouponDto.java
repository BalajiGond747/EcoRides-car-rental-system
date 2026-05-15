package com.ecorides.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {

    private String code;
    private String type;
    private Double value;
    private String expiryDate;
    private Boolean active;
}