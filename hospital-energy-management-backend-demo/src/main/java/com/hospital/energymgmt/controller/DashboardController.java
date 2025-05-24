package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.DashboardSummaryDto;
import com.hospital.energymgmt.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Api(tags = "Dashboard Management", description = "Provides aggregated dashboard data")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @ApiOperation(value = "Get Dashboard Summary", notes = "Retrieves the latest pre-calculated dashboard summary data.")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Adjust roles as needed
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return dashboardService.getDashboardSummary()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Or return a default/empty DTO
    }

    @PostMapping("/refresh-summary")
    @ApiOperation(value = "Refresh Dashboard Summary", notes = "Manually triggers the stored procedure to update the dashboard summary data. Use with caution.")
    @PreAuthorize("hasRole('ADMIN')") // Typically, only admins should refresh this
    public ResponseEntity<Void> refreshDashboardSummary() {
        try {
            dashboardService.refreshDashboardSummary();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.internalServerError().build();
        }
    }
}
