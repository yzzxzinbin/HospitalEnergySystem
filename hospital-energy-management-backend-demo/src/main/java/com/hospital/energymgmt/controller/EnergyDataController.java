package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.model.EnergyData;
import com.hospital.energymgmt.service.EnergyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/energy-data")
public class EnergyDataController {

    @Autowired
    private EnergyDataService energyDataService;

    @PostMapping
    public ResponseEntity<EnergyData> createEnergyData(@RequestBody EnergyData energyData) {
        EnergyData createdData = energyDataService.createEnergyData(energyData);
        return ResponseEntity.ok(createdData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergyData> getEnergyDataById(@PathVariable Long id) {
        EnergyData energyData = energyDataService.getEnergyDataById(id);
        return ResponseEntity.ok(energyData);
    }

    @GetMapping
    public ResponseEntity<List<EnergyData>> getAllEnergyData() {
        List<EnergyData> energyDataList = energyDataService.getAllEnergyData();
        return ResponseEntity.ok(energyDataList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnergyData> updateEnergyData(@PathVariable Long id, @RequestBody EnergyData energyData) {
        EnergyData updatedData = energyDataService.updateEnergyData(id, energyData);
        return ResponseEntity.ok(updatedData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnergyData(@PathVariable Long id) {
        energyDataService.deleteEnergyData(id);
        return ResponseEntity.noContent().build();
    }
}