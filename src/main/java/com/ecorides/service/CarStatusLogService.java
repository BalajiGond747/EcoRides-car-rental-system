package com.ecorides.service;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;

public interface CarStatusLogService {

    void logStatus(Car car, CarStatus status);
}