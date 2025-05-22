package com.hospital.energymgmt.service;

import com.hospital.energymgmt.dto.EnergyDataDto;
import com.hospital.energymgmt.dto.PageResponseDto;
import com.hospital.energymgmt.model.Device;
import com.hospital.energymgmt.model.EnergyData;
import com.hospital.energymgmt.repository.DeviceRepository;
import com.hospital.energymgmt.repository.EnergyDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate; // Changed from jakarta.persistence.criteria.Predicate
import java.time.LocalDateTime;
import java.util.ArrayList; // Added import
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnergyDataService {

    private final EnergyDataRepository energyDataRepository;
    private final DeviceRepository deviceRepository; // To fetch Device entities

    @Autowired
    public EnergyDataService(EnergyDataRepository energyDataRepository, DeviceRepository deviceRepository) {
        this.energyDataRepository = energyDataRepository;
        this.deviceRepository = deviceRepository;
    }

    // Convert EnergyData entity to EnergyDataDto
    private EnergyDataDto convertToDto(EnergyData energyData) {
        if (energyData == null) {
            return null;
        }
        EnergyDataDto dto = new EnergyDataDto();
        dto.setId(energyData.getId());
        dto.setType(energyData.getType());
        dto.setValue(energyData.getValue());
        dto.setTimestamp(energyData.getTimestamp());
        dto.setMeterId(energyData.getMeterId());
        dto.setConsumption(energyData.getConsumption());
        dto.setUnit(energyData.getUnit());
        if (energyData.getDevice() != null) {
            dto.setDeviceId(energyData.getDevice().getId());
            dto.setDeviceName(energyData.getDevice().getName()); // Assuming Device has a getName() method
        }
        return dto;
    }

    // Convert EnergyDataDto to EnergyData entity
    private EnergyData convertToEntity(EnergyDataDto dto, EnergyData entity) {
        entity.setType(dto.getType());
        entity.setValue(dto.getValue());
        entity.setTimestamp(dto.getTimestamp());
        entity.setMeterId(dto.getMeterId());
        entity.setConsumption(dto.getConsumption());
        entity.setUnit(dto.getUnit());

        if (dto.getDeviceId() != null) {
            Device device = deviceRepository.findById(dto.getDeviceId())
                    .orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + dto.getDeviceId()));
            entity.setDevice(device);
        } else {
            entity.setDevice(null); // Allow unassociating a device
        }
        return entity;
    }

    @Transactional
    public EnergyDataDto createEnergyData(EnergyDataDto energyDataDto) {
        EnergyData energyData = new EnergyData();
        convertToEntity(energyDataDto, energyData);
        EnergyData savedEnergyData = energyDataRepository.save(energyData);
        return convertToDto(savedEnergyData);
    }

    @Transactional(readOnly = true)
    public List<EnergyDataDto> getAllEnergyData() {
        return energyDataRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EnergyDataDto> getEnergyDataById(Long id) {
        return energyDataRepository.findById(id).map(this::convertToDto);
    }

    @Transactional
    public EnergyDataDto updateEnergyData(Long id, EnergyDataDto energyDataDto) {
        EnergyData existingEnergyData = energyDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("EnergyData not found with id: " + id));
        
        convertToEntity(energyDataDto, existingEnergyData);
        EnergyData updatedEnergyData = energyDataRepository.save(existingEnergyData);
        return convertToDto(updatedEnergyData);
    }

    @Transactional
    public void deleteEnergyData(Long id) {
        if (!energyDataRepository.existsById(id)) {
            throw new IllegalArgumentException("EnergyData not found with id: " + id);
        }
        energyDataRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EnergyDataDto> getEnergyDataByDeviceId(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + deviceId));
        return energyDataRepository.findByDevice(device).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyDataDto> getEnergyDataByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return energyDataRepository.findByTimestampBetween(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EnergyDataDto> getEnergyDataByDeviceAndTimestampBetween(Long deviceId, LocalDateTime start, LocalDateTime end) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + deviceId));
        return energyDataRepository.findByDeviceAndTimestampBetween(device, start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnergyDataDto> getEnergyDataByType(String type) {
        return energyDataRepository.findByType(type).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponseDto<EnergyDataDto> getAllEnergyData(Pageable pageable, String type, LocalDateTime startDate, LocalDateTime endDate, Long deviceId) {
        Specification<EnergyData> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(type)) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }
            if (deviceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("device").get("id"), deviceId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<EnergyData> energyDataPage = energyDataRepository.findAll(spec, pageable);
        List<EnergyDataDto> energyDataDtos = energyDataPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                energyDataDtos,
                energyDataPage.getTotalElements(),
                energyDataPage.getNumber(),
                energyDataPage.getSize(),
                energyDataPage.getTotalPages()
        );
    }

    // Add more specific query methods as needed, using the repository methods
}