package com.ecorides.service.Impl;

import com.ecorides.domain.CouponType;
import com.ecorides.entity.Coupon;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.mappers.CouponMapper;
import com.ecorides.payload.dto.CouponDTO;
import com.ecorides.repository.CouponRepository;
import com.ecorides.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;

    @Override
    public CouponDTO createCoupon(CouponDTO dto) {

        String code = dto.getCode()
                .trim()
                .toUpperCase();

        if (repository.existsById(code)) {
            throw new BadRequestException("Coupon already exists");
        }

        if (dto.getExpiryDate()
                .isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid expiry date");
        }

        if (dto.getType() == CouponType.PERCENTAGE && dto.getValue()
                .compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new BadRequestException("Percentage discount cannot exceed 100");
        }

        if (dto.getType() == CouponType.FLAT && dto.getValue()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException("Flat discount must be greater than 0");
        }

        dto.setCode(code);

        Coupon coupon = CouponMapper.toEntity(dto);
        Coupon saved = repository.save(coupon);

        return CouponMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon validateCoupon(String code) {

        if (code == null || code.isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }

        String normalizedCode = code.trim()
                .toUpperCase();

        Coupon coupon = repository.findById(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + normalizedCode));

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new BadRequestException("Coupon is inactive");
        }

        if (coupon.getExpiryDate()
                .isBefore(LocalDate.now())) {
            throw new BadRequestException("Coupon has expired");
        }

        return coupon;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {

        return repository.findAll()
                .stream()
                .map(CouponMapper::toDto)
                .toList();

    }

    @Override
    public void activateCoupon(String code) {

        Coupon coupon = getCoupon(code);
        coupon.setIsActive(true);

        repository.save(coupon);

    }

    @Override
    public void deactivateCoupon(String code) {

        Coupon coupon = getCoupon(code);
        coupon.setIsActive(false);

        repository.save(coupon);

    }

    private Coupon getCoupon(String code) {

        if (code == null || code.isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }

        String normalizedCode = code.trim()
                .toUpperCase();

        return repository.findById(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + normalizedCode));
    }
}