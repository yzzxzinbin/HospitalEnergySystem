package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.model.Device;
import com.hospital.energymgmt.model.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long>, JpaSpecificationExecutor<EnergyData> {
    List<EnergyData> findByDevice(Device device);
    List<EnergyData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<EnergyData> findByDeviceAndTimestampBetween(Device device, LocalDateTime start, LocalDateTime end);
    List<EnergyData> findByType(String type);
    List<EnergyData> findByTypeAndTimestampBetween(String type, LocalDateTime start, LocalDateTime end);
    List<EnergyData> findByDeviceAndTypeAndTimestampBetween(Device device, String type, LocalDateTime start, LocalDateTime end);
}