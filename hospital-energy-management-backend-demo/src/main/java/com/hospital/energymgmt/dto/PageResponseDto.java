package com.hospital.energymgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> records;
    private long total;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
