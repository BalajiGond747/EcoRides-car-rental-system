package com.ecorides.service;

import com.ecorides.domain.CouponType;
import com.ecorides.entity.Coupon;
import com.ecorides.payload.dto.CouponDTO;
import com.ecorides.payload.response.PageResponse;

public interface CouponService {

    CouponDTO createCoupon(CouponDTO dto);

    Coupon validateCoupon(String code);

    PageResponse<CouponDTO> getAllCoupons(int page, int size, String search, CouponType type, Boolean isActive, String sortBy, String sortDir);

    void activateCoupon(String code);

    void deactivateCoupon(String code);

}