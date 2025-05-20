package com.hospital.energymgmt.service;

import com.hospital.energymgmt.dto.DeviceTemplateDto;
import com.hospital.energymgmt.model.DeviceTemplate;
import com.hospital.energymgmt.repository.DeviceTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceTemplateService {

    private final DeviceTemplateRepository deviceTemplateRepository;

    @Autowired
    public DeviceTemplateService(DeviceTemplateRepository deviceTemplateRepository) {
        this.deviceTemplateRepository = deviceTemplateRepository;
    }

    // Convert DeviceTemplate entity to DeviceTemplateDto
    private DeviceTemplateDto convertToDto(DeviceTemplate deviceTemplate) {
        if (deviceTemplate == null) {
            return null;
        }
        DeviceTemplateDto dto = new DeviceTemplateDto();
        dto.setId(deviceTemplate.getId());
        dto.setTemplateName(deviceTemplate.getTemplateName());
        dto.setManufacturer(deviceTemplate.getManufacturer());
        dto.setModelIdentifier(deviceTemplate.getModelIdentifier());
        dto.setDeviceCategory(deviceTemplate.getDeviceCategory());
        dto.setDescription(deviceTemplate.getDescription());
        dto.setSpecificationsJson(deviceTemplate.getSpecificationsJson());
        dto.setNominalPowerWatts(deviceTemplate.getNominalPowerWatts());
        dto.setNominalWaterConsumptionLph(deviceTemplate.getNominalWaterConsumptionLph());
        dto.setNominalGasConsumptionM3ph(deviceTemplate.getNominalGasConsumptionM3ph());
        dto.setEstimatedLifespanYears(deviceTemplate.getEstimatedLifespanYears());
        dto.setImageUrl(deviceTemplate.getImageUrl());
        return dto;
    }

    // Convert DeviceTemplateDto to DeviceTemplate entity
    private DeviceTemplate convertToEntity(DeviceTemplateDto dto) {
        if (dto == null) {
            return null;
        }
        DeviceTemplate entity = new DeviceTemplate();
        entity.setId(dto.getId()); // Null for creation, present for update
        entity.setTemplateName(dto.getTemplateName());
        entity.setManufacturer(dto.getManufacturer());
        entity.setModelIdentifier(dto.getModelIdentifier());
        entity.setDeviceCategory(dto.getDeviceCategory());
        entity.setDescription(dto.getDescription());
        entity.setSpecificationsJson(dto.getSpecificationsJson());
        entity.setNominalPowerWatts(dto.getNominalPowerWatts());
        entity.setNominalWaterConsumptionLph(dto.getNominalWaterConsumptionLph());
        entity.setNominalGasConsumptionM3ph(dto.getNominalGasConsumptionM3ph());
        entity.setEstimatedLifespanYears(dto.getEstimatedLifespanYears());
        entity.setImageUrl(dto.getImageUrl());
        return entity;
    }

    @Transactional
    public DeviceTemplateDto createDeviceTemplate(DeviceTemplateDto deviceTemplateDto) {
        // Check for uniqueness of manufacturer and modelIdentifier
        deviceTemplateRepository.findByManufacturerAndModelIdentifier(
                deviceTemplateDto.getManufacturer(), deviceTemplateDto.getModelIdentifier())
                .ifPresent(dt -> {
                    throw new IllegalArgumentException("Device template with manufacturer '" +
                            dt.getManufacturer() + "' and model '" + dt.getModelIdentifier() + "' already exists.");
                });

        DeviceTemplate deviceTemplate = convertToEntity(deviceTemplateDto);
        DeviceTemplate savedDeviceTemplate = deviceTemplateRepository.save(deviceTemplate);
        return convertToDto(savedDeviceTemplate);
    }

    @Transactional(readOnly = true)
    public List<DeviceTemplateDto> getAllDeviceTemplates() {
        return deviceTemplateRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DeviceTemplateDto> getDeviceTemplateById(Long id) {
        return deviceTemplateRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<DeviceTemplateDto> getDeviceTemplatesByManufacturer(String manufacturer) {
        return deviceTemplateRepository.findByManufacturer(manufacturer).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceTemplateDto> getDeviceTemplatesByCategory(String category) {
        return deviceTemplateRepository.findByDeviceCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DeviceTemplateDto> getDeviceTemplateByManufacturerAndModelIdentifier(String manufacturer, String modelIdentifier) {
        return deviceTemplateRepository.findByManufacturerAndModelIdentifier(manufacturer, modelIdentifier)
                .map(this::convertToDto);
    }

    @Transactional
    public DeviceTemplateDto updateDeviceTemplate(Long id, DeviceTemplateDto deviceTemplateDto) {
        DeviceTemplate existingTemplate = deviceTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeviceTemplate not found with id: " + id));

        // Check if updated manufacturer and modelIdentifier conflict with another existing template
        if (deviceTemplateDto.getManufacturer() != null && deviceTemplateDto.getModelIdentifier() != null &&
            (!deviceTemplateDto.getManufacturer().equals(existingTemplate.getManufacturer()) || 
             !deviceTemplateDto.getModelIdentifier().equals(existingTemplate.getModelIdentifier()))) {
            
            deviceTemplateRepository.findByManufacturerAndModelIdentifier(
                deviceTemplateDto.getManufacturer(), deviceTemplateDto.getModelIdentifier())
                .ifPresent(dt -> {
                    if (!dt.getId().equals(id)) { // Ensure it's not the same template
                        throw new IllegalArgumentException("Another device template with manufacturer '" +
                                dt.getManufacturer() + "' and model '" + dt.getModelIdentifier() + "' already exists.");
                    }
                });
            existingTemplate.setManufacturer(deviceTemplateDto.getManufacturer());
            existingTemplate.setModelIdentifier(deviceTemplateDto.getModelIdentifier());
        }

        if (deviceTemplateDto.getTemplateName() != null) {
            existingTemplate.setTemplateName(deviceTemplateDto.getTemplateName());
        }
        if (deviceTemplateDto.getDeviceCategory() != null) {
            existingTemplate.setDeviceCategory(deviceTemplateDto.getDeviceCategory());
        }
        if (deviceTemplateDto.getDescription() != null) {
            existingTemplate.setDescription(deviceTemplateDto.getDescription());
        }
        if (deviceTemplateDto.getSpecificationsJson() != null) {
            existingTemplate.setSpecificationsJson(deviceTemplateDto.getSpecificationsJson());
        }
        if (deviceTemplateDto.getNominalPowerWatts() != null) {
            existingTemplate.setNominalPowerWatts(deviceTemplateDto.getNominalPowerWatts());
        }
        if (deviceTemplateDto.getNominalWaterConsumptionLph() != null) {
            existingTemplate.setNominalWaterConsumptionLph(deviceTemplateDto.getNominalWaterConsumptionLph());
        }
        if (deviceTemplateDto.getNominalGasConsumptionM3ph() != null) {
            existingTemplate.setNominalGasConsumptionM3ph(deviceTemplateDto.getNominalGasConsumptionM3ph());
        }
        if (deviceTemplateDto.getEstimatedLifespanYears() != null) {
            existingTemplate.setEstimatedLifespanYears(deviceTemplateDto.getEstimatedLifespanYears());
        }
        if (deviceTemplateDto.getImageUrl() != null) {
            existingTemplate.setImageUrl(deviceTemplateDto.getImageUrl());
        }

        DeviceTemplate updatedTemplate = deviceTemplateRepository.save(existingTemplate);
        return convertToDto(updatedTemplate);
    }

    @Transactional
    public void deleteDeviceTemplate(Long id) {
        if (!deviceTemplateRepository.existsById(id)) {
            throw new IllegalArgumentException("DeviceTemplate not found with id: " + id);
        }
        // Consider implications: what happens to Devices using this template?
        // Based on DDL (ON DELETE SET NULL), devices.device_template_id will be set to NULL.
        // Additional logic might be needed here if cascading deletes or other actions are required.
        deviceTemplateRepository.deleteById(id);
    }
}
