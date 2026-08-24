package com.ecorides.service;

import com.ecorides.payload.dto.LocationDTO;
import com.ecorides.payload.response.PageResponse;

import java.util.List;

public interface LocationService {

    LocationDTO createLocation(LocationDTO dto);

    LocationDTO getLocationById(Long id);

    PageResponse<LocationDTO> getAllLocations(int page, int size, String search, Boolean isActive);

    List<LocationDTO> getActiveLocations();

    List<LocationDTO> getLocationsByCity(String city);

    LocationDTO updateLocation(Long id, LocationDTO dto);

    void activateLocation(Long id);

    void deactivateLocation(Long id);

}