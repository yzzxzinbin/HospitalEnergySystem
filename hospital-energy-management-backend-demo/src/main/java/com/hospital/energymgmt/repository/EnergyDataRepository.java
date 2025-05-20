package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.model.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    // Additional query methods can be defined here if needed
}