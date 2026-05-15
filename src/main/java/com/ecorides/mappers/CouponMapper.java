package com.ecorides.mappers;

import com.ecorides.entity.Coupon;
import com.ecorides.payload.dto.CouponDto;

import java.time.LocalDate;

public class CouponMapper {

    private CouponMapper() {}

    public static CouponDto toDto(Coupon coupon) {
        if (coupon == null) return null;

        return CouponDto.builder()
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .expiryDate(
                        coupon.getExpiryDate() != null ? coupon.getExpiryDate().toString() : null
                )
                .active(coupon.isActive())
                .build();
    }

    public static Coupon toEntity(CouponDto dto) {
        if (dto == null) return null;

        return Coupon.builder()
                .code(dto.getCode())
                .type(dto.getType())
                .value(dto.getValue())
                .expiryDate(LocalDate.parse(dto.getExpiryDate()))
                .active(true)
                .build();
    }
}