package com.hospital.energymgmt.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Month; // 新增导入

@Entity
@Table(name = "dashboard_summary")
@Data
@AllArgsConstructor // 保留全参构造，以防其他地方使用
public class DashboardSummary {

    @Id
    private Integer id;

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
    private String last7DaysElectricityConsumption;

    @Column(name = "last_7_days_water_consumption", columnDefinition = "json")
    private String last7DaysWaterConsumption;

    @Column(name = "last_7_days_gas_consumption", columnDefinition = "json")
    private String last7DaysGasConsumption;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    // 自定义的无参构造函数，用于测试
    public DashboardSummary() {
        this.id = -1; // 特定的无效ID
        this.onlineDeviceCount = -100;
        this.offlineDeviceCount = -200;
        this.maintenanceDeviceCount = -300;
        this.faultyDeviceCount = -400;
        this.realtimeElectricityPower = -1.0;
        this.realtimeElectricityConsumption = -1.0;
        this.realtimeWaterPower = -1.0;
        this.realtimeWaterConsumption = -1.0;
        this.realtimeGasPower = -1.0;
        this.realtimeGasConsumption = -1.0;
        this.last7DaysElectricityConsumption = "{\"test_marker\": \"electricity_default\"}";
        this.last7DaysWaterConsumption = "{\"test_marker\": \"water_default\"}";
        this.last7DaysGasConsumption = "{\"test_marker\": \"gas_default\"}";
        // 设置一个非常特定的、过去的日期时间作为初始值
        this.lastUpdatedAt = LocalDateTime.of(1999, Month.JANUARY, 1, 10, 30, 0);
    }
}
