package com.ecorides.controller;

import com.ecorides.payload.dto.CouponDto;
import com.ecorides.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CouponController {

    private final CouponService service;

    @PostMapping
    public CouponDto create(@RequestBody CouponDto dto) {
        return service.createCoupon(dto);
    }
}