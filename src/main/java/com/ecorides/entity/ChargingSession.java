package com.ecorides.entity;

import com.ecorides.domain.ChargingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "charging_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal chargeAdded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargingStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}