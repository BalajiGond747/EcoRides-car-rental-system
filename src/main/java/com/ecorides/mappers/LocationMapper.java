package com.ecorides.mappers;

import com.ecorides.entity.Location;
import com.ecorides.payload.dto.LocationDto;

import java.util.List;
import java.util.stream.Collectors;

public class LocationMapper {

    private LocationMapper() {}


    public static LocationDto toDto(Location location) {
        if (location == null) return null;

        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .city(location.getCity())
                .address(location.getAddress())
                .pincode(location.getPincode())
                .active(location.isActive())
                .build();
    }


    public static List<LocationDto> toDtoList(List<Location> locations) {
        if (locations == null || locations.isEmpty()) return List.of();

        return locations.stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }


    public static Location toEntity(LocationDto dto) {
        if (dto == null) return null;

        return Location.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .address(dto.getAddress())
                .pincode(dto.getPincode())
                .active(true)
                .build();
    }


    public static void updateEntity(Location location, LocationDto dto) {
        if (location == null || dto == null) return;

        if (dto.getName() != null) location.setName(dto.getName());
        if (dto.getCity() != null) location.setCity(dto.getCity());
        if (dto.getAddress() != null) location.setAddress(dto.getAddress());
        if (dto.getPincode() != null) location.setPincode(dto.getPincode());
        if (dto.getActive() != null) location.setActive(dto.getActive());
    }
}