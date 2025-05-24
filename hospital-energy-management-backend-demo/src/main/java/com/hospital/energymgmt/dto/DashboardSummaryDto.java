package com.hospital.energymgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private Integer onlineDeviceCount;
    private Integer offlineDeviceCount;
    private Integer maintenanceDeviceCount;
    private Integer faultyDeviceCount;
    private Double realtimeElectricityPower;
    private Double realtimeElectricityConsumption;
    private Double realtimeWaterPower;
    private Double realtimeWaterConsumption;
    private Double realtimeGasPower;
    private Double realtimeGasConsumption;
    private List<DailyConsumptionDto> last7DaysElectricityConsumption;
    private List<DailyConsumptionDto> last7DaysWaterConsumption;
    private List<DailyConsumptionDto> last7DaysGasConsumption;
    private LocalDateTime lastUpdatedAt;
}
