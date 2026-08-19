package com.ecorides.mappers;

import com.ecorides.entity.Location;
import com.ecorides.payload.dto.LocationDTO;

import java.util.List;
import java.util.stream.Collectors;

public class LocationMapper {

    public static LocationDTO toDto(Location location) {

        if (location == null) {
            return null;
        }

        return LocationDTO.builder()
                .id(location.getId())
                .name(location.getName())
                .city(location.getCity())
                .state(location.getState())
                .address(location.getAddress())
                .pincode(location.getPincode())
                .isActive(location.getIsActive())
                .build();

    }

    public static List<LocationDTO> toDtoList(List<Location> locations) {

        if (locations == null || locations.isEmpty()) {
            return List.of();
        }

        return locations.stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Location toEntity(LocationDTO dto) {

        if (dto == null) {
            return null;
        }

        return Location.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .state(dto.getState())
                .address(dto.getAddress())
                .pincode(dto.getPincode())
                .isActive(true)
                .build();

    }

    public static void updateEntity(Location location, LocationDTO dto) {

        if (location == null || dto == null) {
            return;
        }

        if (dto.getName() != null) {
            location.setName(dto.getName());
        }

        if (dto.getCity() != null) {
            location.setCity(dto.getCity());
        }

        if (dto.getState() != null) {
            location.setState(dto.getState());
        }

        if (dto.getAddress() != null) {
            location.setAddress(dto.getAddress());
        }

        if (dto.getPincode() != null) {
            location.setPincode(dto.getPincode());
        }

        if (dto.getIsActive() != null) {
            location.setIsActive(dto.getIsActive());
        }

    }

}