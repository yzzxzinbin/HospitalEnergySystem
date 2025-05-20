package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.DeviceTemplateDto;
import com.hospital.energymgmt.service.DeviceTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-templates")
public class DeviceTemplateController {

    private final DeviceTemplateService deviceTemplateService;

    @Autowired
    public DeviceTemplateController(DeviceTemplateService deviceTemplateService) {
        this.deviceTemplateService = deviceTemplateService;
    }

    @PostMapping
    public ResponseEntity<DeviceTemplateDto> createDeviceTemplate(@RequestBody DeviceTemplateDto deviceTemplateDto) {
        try {
            DeviceTemplateDto createdTemplate = deviceTemplateService.createDeviceTemplate(deviceTemplateDto);
            return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @GetMapping
    public ResponseEntity<List<DeviceTemplateDto>> getAllDeviceTemplates() {
        List<DeviceTemplateDto> templates = deviceTemplateService.getAllDeviceTemplates();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceTemplateDto> getDeviceTemplateById(@PathVariable Long id) {
        return deviceTemplateService.getDeviceTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-manufacturer/{manufacturer}")
    public ResponseEntity<List<DeviceTemplateDto>> getDeviceTemplatesByManufacturer(@PathVariable String manufacturer) {
        List<DeviceTemplateDto> templates = deviceTemplateService.getDeviceTemplatesByManufacturer(manufacturer);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<DeviceTemplateDto>> getDeviceTemplatesByCategory(@PathVariable String category) {
        List<DeviceTemplateDto> templates = deviceTemplateService.getDeviceTemplatesByCategory(category);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/by-manufacturer-model")
    public ResponseEntity<DeviceTemplateDto> getDeviceTemplateByManufacturerAndModel(
            @RequestParam String manufacturer, @RequestParam String modelIdentifier) {
        return deviceTemplateService.getDeviceTemplateByManufacturerAndModelIdentifier(manufacturer, modelIdentifier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceTemplateDto> updateDeviceTemplate(@PathVariable Long id, @RequestBody DeviceTemplateDto deviceTemplateDto) {
        try {
            DeviceTemplateDto updatedTemplate = deviceTemplateService.updateDeviceTemplate(id, deviceTemplateDto);
            return ResponseEntity.ok(updatedTemplate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceTemplate(@PathVariable Long id) {
        try {
            deviceTemplateService.deleteDeviceTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
