package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.EnergyDataDto;
import com.hospital.energymgmt.service.EnergyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/energy-data")
public class EnergyDataController {

    private final EnergyDataService energyDataService;

    @Autowired
    public EnergyDataController(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    @PostMapping
    public ResponseEntity<EnergyDataDto> createEnergyData(@RequestBody EnergyDataDto energyDataDto) {
        try {
            EnergyDataDto createdData = energyDataService.createEnergyData(energyDataDto);
            return new ResponseEntity<>(createdData, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergyDataDto> getEnergyDataById(@PathVariable Long id) {
        return energyDataService.getEnergyDataById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EnergyDataDto>> getAllEnergyData() {
        List<EnergyDataDto> energyDataList = energyDataService.getAllEnergyData();
        return ResponseEntity.ok(energyDataList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnergyDataDto> updateEnergyData(@PathVariable Long id, @RequestBody EnergyDataDto energyDataDto) {
        try {
            EnergyDataDto updatedData = energyDataService.updateEnergyData(id, energyDataDto);
            return ResponseEntity.ok(updatedData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnergyData(@PathVariable Long id) {
        try {
            energyDataService.deleteEnergyData(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-device/{deviceId}")
    public ResponseEntity<List<EnergyDataDto>> getEnergyDataByDevice(@PathVariable Long deviceId) {
        try {
            List<EnergyDataDto> data = energyDataService.getEnergyDataByDeviceId(deviceId);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Device not found
        }
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<EnergyDataDto>> getEnergyDataByType(@PathVariable String type) {
        List<EnergyDataDto> data = energyDataService.getEnergyDataByType(type);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-timestamp")
    public ResponseEntity<List<EnergyDataDto>> getEnergyDataByTimestampRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EnergyDataDto> data = energyDataService.getEnergyDataByTimestampBetween(start, end);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-device-timestamp/{deviceId}")
    public ResponseEntity<List<EnergyDataDto>> getEnergyDataByDeviceAndTimestampRange(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<EnergyDataDto> data = energyDataService.getEnergyDataByDeviceAndTimestampBetween(deviceId, start, end);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Device not found
        }
    }

}