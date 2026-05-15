package com.ecorides.service;

import com.ecorides.entity.Coupon;
import com.ecorides.payload.dto.CouponDto;

public interface CouponService {

    CouponDto createCoupon(CouponDto dto);

    Coupon validateCoupon(String code);
}