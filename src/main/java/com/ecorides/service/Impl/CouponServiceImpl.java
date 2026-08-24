package com.ecorides.service.Impl;

import com.ecorides.domain.CouponType;
import com.ecorides.entity.Coupon;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.mappers.CouponMapper;
import com.ecorides.payload.dto.CouponDTO;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.repository.CouponRepository;
import com.ecorides.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    public PageResponse<CouponDTO> getAllCoupons(int page, int size, String search, CouponType type, Boolean isActive, String sortBy, String sortDir) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Set<String> allowedSortFields = Set.of("code", "value", "expiryDate", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        boolean hasSearch = search != null && !search.trim()
                .isEmpty();

        Page<Coupon> couponPage;

        if (hasSearch && type != null && isActive != null) {

            couponPage = repository.findByCodeContainingIgnoreCaseAndTypeAndIsActive(search.trim(), type, isActive, pageable);

        } else if (hasSearch && type != null) {

            couponPage = repository.findByCodeContainingIgnoreCaseAndType(search.trim(), type, pageable);

        } else if (hasSearch && isActive != null) {

            couponPage = repository.findByCodeContainingIgnoreCaseAndIsActive(search.trim(), isActive, pageable);

        } else if (hasSearch) {

            couponPage = repository.findByCodeContainingIgnoreCase(search.trim(), pageable);

        } else if (type != null && isActive != null) {

            couponPage = repository.findByTypeAndIsActive(type, isActive, pageable);

        } else if (type != null) {

            couponPage = repository.findByType(type, pageable);

        } else if (isActive != null) {

            couponPage = repository.findByIsActive(isActive, pageable);

        } else {

            couponPage = repository.findAll(pageable);
        }

        List<CouponDTO> coupons = couponPage.getContent()
                .stream()
                .map(CouponMapper::toDto)
                .toList();

        return PageResponse.<CouponDTO>builder()
                .content(coupons)
                .page(couponPage.getNumber())
                .size(couponPage.getSize())
                .totalElements(couponPage.getTotalElements())
                .totalPages(couponPage.getTotalPages())
                .first(couponPage.isFirst())
                .last(couponPage.isLast())
                .build();
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