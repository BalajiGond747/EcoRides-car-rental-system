package com.ecorides.entity;

import com.ecorides.domain.CarStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Long carId;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    private LocalDateTime updatedAt;
}