package com.ecorides.service;


import com.ecorides.payload.dto.LocationDto;

import java.util.List;

public interface LocationService {

    LocationDto createLocation(LocationDto dto);

    LocationDto getLocationById(Long id);

    List<LocationDto> getAllLocations();

    List<LocationDto> getActiveLocations();

    List<LocationDto> getLocationsByCity(String city);

    LocationDto updateLocation(Long id, LocationDto dto);

    void deactivateLocation(Long id);
}