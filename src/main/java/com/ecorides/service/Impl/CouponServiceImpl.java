package com.ecorides.service.Impl;

import com.ecorides.entity.Coupon;
import com.ecorides.mappers.CouponMapper;
import com.ecorides.payload.dto.CouponDto;
import com.ecorides.repository.CouponRepository;
import com.ecorides.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;

    @Override
    public CouponDto createCoupon(CouponDto dto) {

        Coupon coupon = CouponMapper.toEntity(dto);
        Coupon saved = repository.save(coupon);

        return CouponMapper.toDto(saved);
    }

    @Override
    public Coupon validateCoupon(String code) {

        Coupon coupon = repository.findById(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon"));

        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon is inactive");
        }

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon expired");
        }

        return coupon;
    }
}