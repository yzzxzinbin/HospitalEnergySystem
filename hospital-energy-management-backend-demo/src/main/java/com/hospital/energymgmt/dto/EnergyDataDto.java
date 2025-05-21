package com.hospital.energymgmt.dto;

import java.time.LocalDateTime;

public class EnergyDataDto {
    private Long id;
    private String type; // e.g., electricity, water, gas
    private Double value; // the amount of energy consumed
    private LocalDateTime timestamp; // when the data was recorded
    private String meterId;
    private Double consumption;
    private String unit;
    private Long deviceId; // ID of the associated device
    private String deviceName; // Optional: for display purposes, populated in service layer

    public EnergyDataDto() {
    }

    public EnergyDataDto(Long id, String type, Double value, LocalDateTime timestamp, String meterId, Double consumption, String unit, Long deviceId, String deviceName) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.meterId = meterId;
        this.consumption = consumption;
        this.unit = unit;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public Double getConsumption() {
        return consumption;
    }

    public void setConsumption(Double consumption) {
        this.consumption = consumption;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}