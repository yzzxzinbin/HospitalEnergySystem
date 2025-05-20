package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.model.DeviceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTemplateRepository extends JpaRepository<DeviceTemplate, Long> {
    List<DeviceTemplate> findByManufacturer(String manufacturer);
    List<DeviceTemplate> findByDeviceCategory(String deviceCategory);
    Optional<DeviceTemplate> findByManufacturerAndModelIdentifier(String manufacturer, String modelIdentifier);
}
