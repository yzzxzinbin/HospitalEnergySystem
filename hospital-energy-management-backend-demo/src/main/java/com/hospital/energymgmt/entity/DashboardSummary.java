package com.hospital.energymgmt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

    @Id
    private Integer id; // Usually 1

    @Column(name = "online_device_count")
    private Integer onlineDeviceCount;

    @Column(name = "offline_device_count")
    private Integer offlineDeviceCount;

    @Column(name = "maintenance_device_count")
    private Integer maintenanceDeviceCount;

    @Column(name = "faulty_device_count")
    private Integer faultyDeviceCount;

    @Column(name = "realtime_electricity_power")
    private Double realtimeElectricityPower;

    @Column(name = "realtime_electricity_consumption")
    private Double realtimeElectricityConsumption;

    @Column(name = "realtime_water_power")
    private Double realtimeWaterPower;

    @Column(name = "realtime_water_consumption")
    private Double realtimeWaterConsumption;

    @Column(name = "realtime_gas_power")
    private Double realtimeGasPower;

    @Column(name = "realtime_gas_consumption")
    private Double realtimeGasConsumption;

    @Column(name = "last_7_days_electricity_consumption", columnDefinition = "json")
    private String last7DaysElectricityConsumption; // Store as JSON string

    @Column(name = "last_7_days_water_consumption", columnDefinition = "json")
    private String last7DaysWaterConsumption; // Store as JSON string

    @Column(name = "last_7_days_gas_consumption", columnDefinition = "json")
    private String last7DaysGasConsumption; // Store as JSON string

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
