package com.hospital.energymgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.energymgmt.dto.DailyConsumptionDto;
import com.hospital.energymgmt.dto.DashboardSummaryDto;
import com.hospital.energymgmt.entity.DashboardSummary;
import com.hospital.energymgmt.repository.DashboardSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final DashboardSummaryRepository dashboardSummaryRepository;
    private final ObjectMapper objectMapper; // For parsing JSON strings

    @Autowired
    public DashboardService(DashboardSummaryRepository dashboardSummaryRepository, ObjectMapper objectMapper) {
        this.dashboardSummaryRepository = dashboardSummaryRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<DashboardSummaryDto> getDashboardSummary() {
        Optional<DashboardSummary> summaryOptional = dashboardSummaryRepository.findCurrentDashboardSummary();
        return summaryOptional.map(this::convertToDto);
    }

    private DashboardSummaryDto convertToDto(DashboardSummary entity) {
        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setOnlineDeviceCount(entity.getOnlineDeviceCount());
        dto.setOfflineDeviceCount(entity.getOfflineDeviceCount());
        dto.setMaintenanceDeviceCount(entity.getMaintenanceDeviceCount());
        dto.setFaultyDeviceCount(entity.getFaultyDeviceCount());
        dto.setRealtimeElectricityPower(entity.getRealtimeElectricityPower());
        dto.setRealtimeElectricityConsumption(entity.getRealtimeElectricityConsumption());
        dto.setRealtimeWaterPower(entity.getRealtimeWaterPower());
        dto.setRealtimeWaterConsumption(entity.getRealtimeWaterConsumption());
        dto.setRealtimeGasPower(entity.getRealtimeGasPower());
        dto.setRealtimeGasConsumption(entity.getRealtimeGasConsumption());
        dto.setLastUpdatedAt(entity.getLastUpdatedAt());

        // Parse JSON strings to List<DailyConsumptionDto>
        dto.setLast7DaysElectricityConsumption(parseConsumptionList(entity.getLast7DaysElectricityConsumption(), "electricity"));
        dto.setLast7DaysWaterConsumption(parseConsumptionList(entity.getLast7DaysWaterConsumption(), "water"));
        dto.setLast7DaysGasConsumption(parseConsumptionList(entity.getLast7DaysGasConsumption(), "gas"));

        return dto;
    }

    private List<DailyConsumptionDto> parseConsumptionList(String jsonString, String type) {
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<DailyConsumptionDto>>() {});
        } catch (IOException e) {
            logger.error("Failed to parse {} consumption data from JSON: {}\nError: {}", type, jsonString, e.getMessage());
            return Collections.emptyList(); // Or throw a custom exception
        }
    }

    /**
     * Method to explicitly trigger the stored procedure.
     * Consider the implications of calling this directly via an API endpoint.
     * It's often better to have a scheduled job in the database or application
     * to update the summary table periodically.
     */
    @Transactional
    public void refreshDashboardSummary() {
        dashboardSummaryRepository.callUpdateDashboardSummary();
        logger.info("Successfully called UpdateDashboardSummary stored procedure.");
    }
}
