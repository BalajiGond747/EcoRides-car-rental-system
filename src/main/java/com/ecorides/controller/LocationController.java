package com.ecorides.controller;

import com.ecorides.payload.dto.LocationDto;
import com.ecorides.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public LocationDto createLocation(@RequestBody LocationDto dto) {
        return locationService.createLocation(dto);
    }

    @GetMapping("/{id}")
    public LocationDto getLocation(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }

    @GetMapping
    public List<LocationDto> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/active")
    public List<LocationDto> getActiveLocations() {
        return locationService.getActiveLocations();
    }

    @GetMapping("/city/{city}")
    public List<LocationDto> getByCity(@PathVariable String city) {
        return locationService.getLocationsByCity(city);
    }

    @PutMapping("/{id}")
    public LocationDto updateLocation(@PathVariable Long id,
                                      @RequestBody LocationDto dto) {
        return locationService.updateLocation(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        locationService.deactivateLocation(id);
    }
}