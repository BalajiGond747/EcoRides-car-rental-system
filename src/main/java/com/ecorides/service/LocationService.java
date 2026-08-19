package com.ecorides.service;

import com.ecorides.payload.dto.LocationDTO;

import java.util.List;

public interface LocationService {

    LocationDTO createLocation(LocationDTO dto);

    LocationDTO getLocationById(Long id);

    List<LocationDTO> getAllLocations();

    List<LocationDTO> getActiveLocations();

    List<LocationDTO> getLocationsByCity(String city);

    LocationDTO updateLocation(Long id, LocationDTO dto);

    void activateLocation(Long id);

    void deactivateLocation(Long id);

}