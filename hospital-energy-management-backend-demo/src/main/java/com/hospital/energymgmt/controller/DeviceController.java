package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.DeviceDto;
import com.hospital.energymgmt.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto deviceDto) {
        try {
            DeviceDto createdDevice = deviceService.createDevice(deviceDto);
            return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @GetMapping
    public ResponseEntity<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable Long id, @RequestBody DeviceDto deviceDto) {
        try {
            DeviceDto updatedDevice = deviceService.updateDevice(id, deviceDto);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-room/{roomId}")
    public ResponseEntity<List<DeviceDto>> getDevicesByRoomId(@PathVariable Long roomId) {
        try {
            List<DeviceDto> devices = deviceService.getDevicesByRoomId(roomId);
            return ResponseEntity.ok(devices);
        } catch (IllegalArgumentException e) { // Catches room not found
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-template/{templateId}")
    public ResponseEntity<List<DeviceDto>> getDevicesByDeviceTemplateId(@PathVariable Long templateId) {
         try {
            List<DeviceDto> devices = deviceService.getDevicesByDeviceTemplateId(templateId);
            return ResponseEntity.ok(devices);
        } catch (IllegalArgumentException e) { // Catches template not found
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<DeviceDto>> getDevicesByStatus(@PathVariable String status) {
        List<DeviceDto> devices = deviceService.getDevicesByStatus(status);
        return ResponseEntity.ok(devices);
    }
}
