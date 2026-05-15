package com.ecorides.entity;

import com.ecorides.domain.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private String type;
    private String description;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    private LocalDate startDate;

    private LocalDate endDate;
}