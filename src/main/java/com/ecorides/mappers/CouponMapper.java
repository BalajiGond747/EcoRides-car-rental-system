package com.ecorides.mappers;

import com.ecorides.entity.Coupon;
import com.ecorides.payload.dto.CouponDTO;

public class CouponMapper {

    public static CouponDTO toDto(Coupon coupon) {

        if (coupon == null) {
            return null;
        }

        return CouponDTO.builder()
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .expiryDate(coupon.getExpiryDate())
                .isActive(coupon.getIsActive())
                .build();
    }

    public static Coupon toEntity(CouponDTO dto) {

        if (dto == null) {
            return null;
        }

        return Coupon.builder()
                .code(dto.getCode())
                .type(dto.getType())
                .value(dto.getValue())
                .expiryDate(dto.getExpiryDate())
                .isActive(true)
                .build();

    }

    public static void updateEntity(Coupon coupon, CouponDTO dto) {

        if (coupon == null || dto == null) {
            return;
        }

        if (dto.getType() != null) {
            coupon.setType(dto.getType());
        }

        if (dto.getValue() != null) {
            coupon.setValue(dto.getValue());
        }

        if (dto.getExpiryDate() != null) {
            coupon.setExpiryDate(dto.getExpiryDate());
        }

        if (dto.getIsActive() != null) {
            coupon.setIsActive(dto.getIsActive());
        }

    }

}