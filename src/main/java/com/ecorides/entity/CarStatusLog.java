package com.ecorides.entity;

import com.ecorides.domain.CarStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_status_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long carId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

}