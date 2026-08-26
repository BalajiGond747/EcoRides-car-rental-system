package com.ecorides.controller;

import com.ecorides.domain.CouponType;
import com.ecorides.payload.dto.CouponDTO;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.security.CustomUserDetailsService;
import com.ecorides.security.JwtAuthenticationFilter;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService service;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void shouldGetAllCouponsSuccessfully() throws Exception {

        PageResponse<CouponDTO> pageResponse = PageResponse.<CouponDTO>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(service.getAllCoupons(0, 10, null, null, null, "createdAt", "desc")).thenReturn(pageResponse);

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Coupons fetched successfully"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCouponSuccessfully() throws Exception {

        CouponDTO request = CouponDTO.builder()
                .code("SAVE10")
                .type(CouponType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .expiryDate(LocalDate.now()
                        .plusDays(30))
                .isActive(true)
                .build();

        CouponDTO response = CouponDTO.builder()
                .code("SAVE10")
                .type(CouponType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .expiryDate(LocalDate.now()
                        .plusDays(30))
                .isActive(true)
                .build();

        when(service.createCoupon(any(CouponDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/coupons").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Coupon created successfully"))
                .andExpect(jsonPath("$.data.code").value("SAVE10"))
                .andExpect(jsonPath("$.data.type").value("PERCENTAGE"))
                .andExpect(jsonPath("$.data.value").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldActivateCouponSuccessfully() throws Exception {

        mockMvc.perform(put("/api/coupons/SAVE10/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Coupon activated"));

        verify(service).activateCoupon("SAVE10");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateCouponSuccessfully() throws Exception {

        mockMvc.perform(put("/api/coupons/SAVE10/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Coupon deactivated"));

        verify(service).deactivateCoupon("SAVE10");
    }
}