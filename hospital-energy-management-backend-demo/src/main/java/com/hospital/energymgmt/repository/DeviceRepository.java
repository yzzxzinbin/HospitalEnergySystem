package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.model.Device;
import com.hospital.energymgmt.model.DeviceTemplate;
import com.hospital.energymgmt.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByRoom(Room room);
    List<Device> findByDeviceTemplate(DeviceTemplate deviceTemplate);
    List<Device> findByStatus(String status);
}
