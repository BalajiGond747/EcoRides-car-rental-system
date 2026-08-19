package com.ecorides.service.Impl;

import com.ecorides.entity.Location;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ConflictException;
import com.ecorides.exception.LocationException;
import com.ecorides.mappers.LocationMapper;
import com.ecorides.payload.dto.LocationDTO;
import com.ecorides.repository.LocationRepository;
import com.ecorides.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationDTO createLocation(LocationDTO dto) {

        String name = dto.getName()
                .trim();

        if (locationRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Location already exists");
        }

        dto.setName(name);

        Location location = LocationMapper.toEntity(dto);
        Location saved = locationRepository.save(location);
        return LocationMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDTO getLocationById(Long id) {

        return LocationMapper.toDto(getLocationEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> getAllLocations() {

        return LocationMapper.toDtoList(locationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> getActiveLocations() {

        return LocationMapper.toDtoList(locationRepository.findByIsActiveTrue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> getLocationsByCity(String city) {

        if (city == null || city.isBlank()) {
            throw new BadRequestException("City cannot be empty");
        }

        return LocationMapper.toDtoList(locationRepository.findByCityIgnoreCase(city.trim()));
    }

    @Override
    public LocationDTO updateLocation(Long id, LocationDTO dto) {

        Location location = getLocationEntity(id);

        if (dto.getName() != null) {

            String newName = dto.getName()
                    .trim();

            if (!newName.equalsIgnoreCase(location.getName()) && locationRepository.existsByNameIgnoreCase(newName)) {

                throw new ConflictException("Location name already exists");
            }

            dto.setName(newName);
        }

        LocationMapper.updateEntity(location, dto);

        Location updated = locationRepository.save(location);

        return LocationMapper.toDto(updated);
    }

    @Override
    public void activateLocation(Long id) {

        Location location = getLocationEntity(id);
        location.setIsActive(true);

        locationRepository.save(location);
    }

    @Override
    public void deactivateLocation(Long id) {

        Location location = getLocationEntity(id);
        location.setIsActive(false);

        locationRepository.save(location);
    }

    private Location getLocationEntity(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid location id");
        }

        return locationRepository.findById(id)
                .orElseThrow(() -> new LocationException("Location not found"));
    }
}