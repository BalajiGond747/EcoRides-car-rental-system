package com.ecorides.repository;

import com.ecorides.domain.CouponType;
import com.ecorides.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    List<Coupon> findByIsActiveTrue();

    Page<Coupon> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    Page<Coupon> findByCodeContainingIgnoreCaseAndType(String code, CouponType type, Pageable pageable);

    Page<Coupon> findByCodeContainingIgnoreCaseAndIsActive(String code, Boolean isActive, Pageable pageable);

    Page<Coupon> findByCodeContainingIgnoreCaseAndTypeAndIsActive(String code, CouponType type, Boolean isActive, Pageable pageable);

    Page<Coupon> findByType(CouponType type, Pageable pageable);

    Page<Coupon> findByTypeAndIsActive(CouponType type, Boolean isActive, Pageable pageable);

    Page<Coupon> findByIsActive(Boolean isActive, Pageable pageable);
}