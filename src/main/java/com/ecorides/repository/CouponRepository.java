package com.ecorides.repository;

import com.ecorides.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    List<Coupon> findByIsActiveTrue();
}