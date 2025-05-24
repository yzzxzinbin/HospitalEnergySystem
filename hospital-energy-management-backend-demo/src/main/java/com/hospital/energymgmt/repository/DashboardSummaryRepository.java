package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.entity.DashboardSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardSummaryRepository extends JpaRepository<DashboardSummary, Integer> {

    // The dashboard_summary table is expected to have only one row with id = 1
    default Optional<DashboardSummary> findCurrentDashboardSummary() {
        return findById(1);
    }

    // Optional: If you want to explicitly call the stored procedure via JPA
    // Note: Calling stored procedures that modify data might be better handled by a scheduled task
    // or a database event rather than directly in a typical GET request flow.
    @Query(value = "CALL UpdateDashboardSummary()", nativeQuery = true)
    void callUpdateDashboardSummary();
}
