package com.ecorides.service.Impl;

import com.ecorides.entity.Location;
import com.ecorides.exception.LocationException;
import com.ecorides.mappers.LocationMapper;
import com.ecorides.payload.dto.LocationDto;
import com.ecorides.repository.LocationRepository;
import com.ecorides.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public LocationDto createLocation(LocationDto dto) {

        Location location = LocationMapper.toEntity(dto);

        return LocationMapper.toDto(locationRepository.save(location));
    }

    @Override
    public LocationDto getLocationById(Long id) {
        return LocationMapper.toDto(getLocationEntity(id));
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return LocationMapper.toDtoList(locationRepository.findAll());
    }

    @Override
    public List<LocationDto> getActiveLocations() {
        return LocationMapper.toDtoList(locationRepository.findByActiveTrue());
    }

    @Override
    public List<LocationDto> getLocationsByCity(String city) {

        if (city == null || city.isBlank()) {
            throw new LocationException("City cannot be empty");
        }

        return LocationMapper.toDtoList(locationRepository.findByCityIgnoreCase(city));
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long id, LocationDto dto) {

        Location location = getLocationEntity(id);

        LocationMapper.updateEntity(location, dto);

        return LocationMapper.toDto(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void deactivateLocation(Long id) {

        Location location = getLocationEntity(id);

        location.setActive(false);

        locationRepository.save(location);
    }


    private Location getLocationEntity(Long id) {

        if (id == null || id <= 0) {
            throw new LocationException("Invalid location ID");
        }

        return locationRepository.findById(id)
                .orElseThrow(() -> new LocationException("Location not found"));
    }
}