package com.hospital.energymgmt.dto;

import java.time.LocalDateTime;

public class EnergyDataDto {
    private Long id;
    private String type; // e.g., electricity, water, gas
    private Double value; // the amount of energy consumed
    private LocalDateTime timestamp; // when the data was recorded

    public EnergyDataDto() {
    }

    public EnergyDataDto(Long id, String type, Double value, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

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
}