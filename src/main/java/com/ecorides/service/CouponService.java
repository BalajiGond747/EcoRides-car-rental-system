package com.ecorides.service;

import com.ecorides.entity.Coupon;
import com.ecorides.payload.dto.CouponDTO;

import java.util.List;

public interface CouponService {

    CouponDTO createCoupon(CouponDTO dto);

    Coupon validateCoupon(String code);

    List<CouponDTO> getAllCoupons();

    void activateCoupon(String code);

    void deactivateCoupon(String code);

}