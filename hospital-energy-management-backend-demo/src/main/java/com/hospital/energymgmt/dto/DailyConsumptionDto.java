package com.hospital.energymgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyConsumptionDto {
    private String date;
    private Double consumption;
}
