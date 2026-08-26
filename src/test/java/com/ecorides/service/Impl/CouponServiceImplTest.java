package com.ecorides.service.Impl;

import com.ecorides.domain.CouponType;
import com.ecorides.entity.Coupon;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository repository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon activeCoupon;

    @BeforeEach
    void setUp() {

        activeCoupon = new Coupon();

        activeCoupon.setCode("SAVE10");
        activeCoupon.setType(CouponType.PERCENTAGE);
        activeCoupon.setValue(new java.math.BigDecimal("10"));
        activeCoupon.setIsActive(true);
        activeCoupon.setExpiryDate(LocalDate.now()
                .plusDays(10));
    }

    @Test
    void shouldValidateActiveNonExpiredCoupon() {

        when(repository.findById("SAVE10")).thenReturn(Optional.of(activeCoupon));

        Coupon result = couponService.validateCoupon(" save10 ");

        assertEquals("SAVE10", result.getCode());
        assertTrue(result.getIsActive());
    }

    @Test
    void shouldRejectBlankCouponCode() {

        BadRequestException exception = assertThrows(BadRequestException.class, () -> couponService.validateCoupon("   "));

        assertEquals("Coupon code is required", exception.getMessage());

        verify(repository, never()).findById(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldRejectNonExistingCoupon() {

        when(repository.findById("SAVE10")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> couponService.validateCoupon("save10"));

        assertEquals("Coupon not found: SAVE10", exception.getMessage());
    }

    @Test
    void shouldRejectInactiveCoupon() {

        activeCoupon.setIsActive(false);

        when(repository.findById("SAVE10")).thenReturn(Optional.of(activeCoupon));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> couponService.validateCoupon("SAVE10"));

        assertEquals("Coupon is inactive", exception.getMessage());
    }

    @Test
    void shouldRejectExpiredCoupon() {

        activeCoupon.setExpiryDate(LocalDate.now()
                .minusDays(1));

        when(repository.findById("SAVE10")).thenReturn(Optional.of(activeCoupon));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> couponService.validateCoupon("SAVE10"));

        assertEquals("Coupon has expired", exception.getMessage());
    }

    @Test
    void shouldDeactivateCoupon() {

        when(repository.findById("SAVE10")).thenReturn(Optional.of(activeCoupon));

        couponService.deactivateCoupon("save10");

        assertTrue(!activeCoupon.getIsActive());

        verify(repository).save(activeCoupon);
    }

    @Test
    void shouldActivateCoupon() {

        activeCoupon.setIsActive(false);

        when(repository.findById("SAVE10")).thenReturn(Optional.of(activeCoupon));

        couponService.activateCoupon("save10");

        assertTrue(activeCoupon.getIsActive());

        verify(repository).save(activeCoupon);
    }
}