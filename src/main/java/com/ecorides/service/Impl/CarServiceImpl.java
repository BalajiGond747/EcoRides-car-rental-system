package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.Location;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.CarException;
import com.ecorides.exception.ConflictException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.mappers.CarMapper;
import com.ecorides.payload.dto.CarDTO;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.LocationRepository;
import com.ecorides.service.CarService;
import com.ecorides.service.CarStatusLogService;
import com.ecorides.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final LocationRepository locationRepository;
    private final CarStatusLogService carStatusLogService;
    private final ImageStorageService imageStorageService;

    @Override
    public CarDTO createCar(CarDTO dto, MultipartFile image) {

        String registrationNumber = dto.getRegistrationNumber()
                .trim()
                .toUpperCase();

        if (carRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new ConflictException("Car with this registration already exists");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        dto.setRegistrationNumber(registrationNumber);

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Car image is required");
        }

        String imageUrl = imageStorageService.storeCarImage(image);
        dto.setImageUrl(imageUrl);

        Car car = CarMapper.toEntity(dto);
        car.setLocation(location);
        Car saved = carRepository.save(car);

        return CarMapper.toDTO(saved);
    }

    @Override
    public CarDTO updateCar(Long carId, CarDTO dto, MultipartFile image) {

        Car car = getCar(carId);

        if (dto.getRegistrationNumber() != null) {

            String registrationNumber = dto.getRegistrationNumber()
                    .trim()
                    .toUpperCase();

            if (!registrationNumber.equals(car.getRegistrationNumber()) && carRepository.existsByRegistrationNumber(registrationNumber)) {

                throw new ConflictException("Registration number already exists");
            }

            dto.setRegistrationNumber(registrationNumber);
        }

        if (dto.getLocationId() != null) {

            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

            car.setLocation(location);
        }

        CarMapper.updateEntity(car, dto);

        if (image != null && !image.isEmpty()) {

            String oldImageUrl = car.getImageUrl();
            String newImageUrl = imageStorageService.storeCarImage(image);

            car.setImageUrl(newImageUrl);

            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                imageStorageService.deleteCarImage(oldImageUrl);
            }
        }

        return CarMapper.toDTO(carRepository.save(car));
    }

    @Override
    public void activateCar(Long carId) {

        Car car = getCar(carId);
        car.setIsActive(true);

        carRepository.save(car);
    }

    @Override
    public void deactivateCar(Long carId) {

        Car car = getCar(carId);
        car.setIsActive(false);

        carRepository.save(car);
    }

    @Override
    public CarDTO updateCarStatus(Long carId, CarStatus status) {

        if (status == null) {
            throw new CarException("Car status cannot be null");
        }

        Car car = getCar(carId);
        car.setStatus(status);
        carStatusLogService.logStatus(car, status);

        return CarMapper.toDTO(carRepository.save(car));
    }

    @Override
    public CarDTO getCarById(Long carId) {

        return CarMapper.toDTO(getCar(carId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CarDTO> getAllCars(int page, int size, String search, String category, CarStatus status, Boolean isActive, String sortBy, String sortDir) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 9;
        }

        if (size > 100) {
            size = 100;
        }

        Set<String> allowedSortFields = Set.of("id", "name", "category", "pricePerDay", "batteryLevel", "rangeKm", "seatingCapacity", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        boolean hasSearch = search != null && !search.trim()
                .isEmpty();

        boolean hasCategory = category != null && !category.trim()
                .isEmpty();

        Page<Car> carPage;

        if (hasSearch && hasCategory && status != null && isActive != null) {

            carPage = carRepository.findBySearchAndCategoryAndStatusAndIsActive(search.trim(), category.trim(), status, isActive, pageable);

        } else if (hasSearch && hasCategory && status != null) {

            carPage = carRepository.findBySearchAndCategoryAndStatus(search.trim(), category.trim(), status, pageable);

        } else if (hasSearch && hasCategory) {

            carPage = carRepository.findBySearchAndCategory(search.trim(), category.trim(), pageable);

        } else if (hasSearch) {

            carPage = carRepository.findBySearch(search.trim(), pageable);

        } else if (hasCategory && status != null && isActive != null) {

            carPage = carRepository.findByCategoryAndStatusAndIsActive(category.trim(), status, isActive, pageable);

        } else if (hasCategory && status != null) {

            carPage = carRepository.findByCategoryAndStatus(category.trim(), status, pageable);

        } else if (hasCategory) {

            carPage = carRepository.findByCategory(category.trim(), pageable);

        } else if (status != null && isActive != null) {

            carPage = carRepository.findByStatusAndIsActive(status, isActive, pageable);

        } else if (status != null) {

            carPage = carRepository.findByStatus(status, pageable);

        } else if (isActive != null) {

            carPage = carRepository.findByIsActive(isActive, pageable);

        } else {

            carPage = carRepository.findAll(pageable);
        }

        List<CarDTO> cars = CarMapper.toDTOList(carPage.getContent());

        return PageResponse.<CarDTO>builder()
                .content(cars)
                .page(carPage.getNumber())
                .size(carPage.getSize())
                .totalElements(carPage.getTotalElements())
                .totalPages(carPage.getTotalPages())
                .first(carPage.isFirst())
                .last(carPage.isLast())
                .build();
    }

    @Override
    public List<CarDTO> getAvailableCars() {

        return CarMapper.toDTOList(carRepository.findByIsActiveTrueAndStatus(CarStatus.AVAILABLE));
    }

    @Override
    public List<CarDTO> getCarsByLocation(Long locationId) {

        if (locationId == null || locationId <= 0) {
            throw new BadRequestException("Invalid location id");
        }

        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location not found with id: " + locationId);
        }

        return CarMapper.toDTOList(carRepository.findByLocationIdAndIsActiveTrue(locationId));
    }

    @Override
    public List<CarDTO> getCarsByCategory(String category) {

        if (category == null || category.isBlank()) {
            throw new BadRequestException("Category cannot be empty");
        }

        return CarMapper.toDTOList(carRepository.findByCategoryAndIsActiveTrue(category.trim()));
    }

    private Car getCar(Long carId) {

        if (carId == null || carId <= 0) {
            throw new BadRequestException("Invalid car id");
        }

        return carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
    }
}