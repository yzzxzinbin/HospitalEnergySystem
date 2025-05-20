package com.hospital.energymgmt.service;

import com.hospital.energymgmt.dto.DeviceDto;
import com.hospital.energymgmt.model.Device;
import com.hospital.energymgmt.model.DeviceTemplate;
import com.hospital.energymgmt.model.Room;
import com.hospital.energymgmt.repository.DeviceRepository;
import com.hospital.energymgmt.repository.DeviceTemplateRepository;
import com.hospital.energymgmt.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final RoomRepository roomRepository;
    private final DeviceTemplateRepository deviceTemplateRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository,
                         RoomRepository roomRepository,
                         DeviceTemplateRepository deviceTemplateRepository) {
        this.deviceRepository = deviceRepository;
        this.roomRepository = roomRepository;
        this.deviceTemplateRepository = deviceTemplateRepository;
    }

    // Convert Device entity to DeviceDto
    private DeviceDto convertToDto(Device device) {
        if (device == null) {
            return null;
        }
        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setInstallationDate(device.getInstallationDate());
        dto.setStatus(device.getStatus());

        if (device.getRoom() != null) {
            dto.setRoomId(device.getRoom().getId());
            dto.setRoomNumber(device.getRoom().getRoomNumber());
        }
        if (device.getDeviceTemplate() != null) {
            dto.setDeviceTemplateId(device.getDeviceTemplate().getId());
            dto.setDeviceTemplateName(device.getDeviceTemplate().getTemplateName());
        }
        return dto;
    }

    // Convert DeviceDto to Device entity (partially, for create/update)
    // Associations (Room, DeviceTemplate) are handled in service methods
    private Device convertToEntity(DeviceDto dto, Device entity) {
        entity.setName(dto.getName());
        entity.setInstallationDate(dto.getInstallationDate());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    @Transactional
    public DeviceDto createDevice(DeviceDto deviceDto) {
        Device device = new Device();
        convertToEntity(deviceDto, device);

        if (deviceDto.getRoomId() != null) {
            Room room = roomRepository.findById(deviceDto.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + deviceDto.getRoomId()));
            device.setRoom(room);
        }

        if (deviceDto.getDeviceTemplateId() != null) {
            DeviceTemplate template = deviceTemplateRepository.findById(deviceDto.getDeviceTemplateId())
                    .orElseThrow(() -> new IllegalArgumentException("DeviceTemplate not found with id: " + deviceDto.getDeviceTemplateId()));
            device.setDeviceTemplate(template);
        }

        Device savedDevice = deviceRepository.save(device);
        return convertToDto(savedDevice);
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DeviceDto> getDeviceById(Long id) {
        return deviceRepository.findById(id).map(this::convertToDto);
    }

    @Transactional
    public DeviceDto updateDevice(Long id, DeviceDto deviceDto) {
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + id));

        convertToEntity(deviceDto, existingDevice);

        if (deviceDto.getRoomId() != null) {
            if (existingDevice.getRoom() == null || !existingDevice.getRoom().getId().equals(deviceDto.getRoomId())) {
                Room room = roomRepository.findById(deviceDto.getRoomId())
                        .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + deviceDto.getRoomId()));
                existingDevice.setRoom(room);
            }
        } else {
            existingDevice.setRoom(null); // Allow unassigning from a room
        }

        if (deviceDto.getDeviceTemplateId() != null) {
            if (existingDevice.getDeviceTemplate() == null || !existingDevice.getDeviceTemplate().getId().equals(deviceDto.getDeviceTemplateId())) {
                DeviceTemplate template = deviceTemplateRepository.findById(deviceDto.getDeviceTemplateId())
                        .orElseThrow(() -> new IllegalArgumentException("DeviceTemplate not found with id: " + deviceDto.getDeviceTemplateId()));
                existingDevice.setDeviceTemplate(template);
            }
        } else {
            existingDevice.setDeviceTemplate(null); // Allow unassigning template
        }

        Device updatedDevice = deviceRepository.save(existingDevice);
        return convertToDto(updatedDevice);
    }

    @Transactional
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new IllegalArgumentException("Device not found with id: " + id);
        }
        // Consider implications: EnergyData associated with this device will have device_id set to NULL due to DDL.
        deviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getDevicesByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        return deviceRepository.findByRoom(room).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getDevicesByDeviceTemplateId(Long templateId) {
        DeviceTemplate template = deviceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("DeviceTemplate not found with id: " + templateId));
        return deviceRepository.findByDeviceTemplate(template).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getDevicesByStatus(String status) {
        return deviceRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
