package com.hospital.energymgmt.service;

import com.hospital.energymgmt.model.EnergyData;
import com.hospital.energymgmt.repository.EnergyDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyDataService {

    @Autowired
    public EnergyDataRepository energyDataRepository;

    public EnergyData createEnergyData(EnergyData energyData) {
        // Assuming ID is null or not set for new data, save will create a new entry.
        return energyDataRepository.save(energyData);
    }

    public List<EnergyData> getAllEnergyData() {
        return energyDataRepository.findAll();
    }

    public EnergyData saveEnergyData(EnergyData energyData) {
        return energyDataRepository.save(energyData);
    }

    public EnergyData updateEnergyData(Long id, EnergyData energyDataDetails) {
        EnergyData energyData = energyDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EnergyData not found with id " + id)); // Or handle more gracefully

        // Update fields from energyDataDetails to energyData
        if (energyDataDetails.getTimestamp() != null) {
            energyData.setTimestamp(energyDataDetails.getTimestamp());
        }
        if (energyDataDetails.getType() != null) {
            energyData.setType(energyDataDetails.getType());
        }
        if (energyDataDetails.getValue() != null) {
            energyData.setValue(energyDataDetails.getValue());
        }
        if (energyDataDetails.getMeterId() != null) { 
            energyData.setMeterId(energyDataDetails.getMeterId());
        }
        if (energyDataDetails.getConsumption() != null) {
            energyData.setConsumption(energyDataDetails.getConsumption());
        }
        if (energyDataDetails.getUnit() != null) {
            energyData.setUnit(energyDataDetails.getUnit());
        }
        // Add other fields as necessary

        return energyDataRepository.save(energyData);
    }

    public EnergyData getEnergyDataById(Long id) {
        return energyDataRepository.findById(id).orElse(null);
    }

    public void deleteEnergyData(Long id) {
        energyDataRepository.deleteById(id);
    }

    // Additional methods for data analysis can be added here
}