
package com.bikehealth.controller;

import com.bikehealth.entity.Maintenance;
import com.bikehealth.repository.MaintenanceRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "http://localhost:5173")
public class HealthController {

    private final MaintenanceRepository maintenanceRepository;

    public HealthController(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @GetMapping("/{bikeId}")
    public Map<String, Object> getHealthScore(@PathVariable Long bikeId) {

        List<Maintenance> records =
                maintenanceRepository.findByBikeId(bikeId);

        int healthScore = 100;

        for (Maintenance record : records) {
            if (record.getNextDueDate() != null
                    && record.getNextDueDate().isBefore(LocalDate.now())) {
                healthScore -= 15;
            } else if ("PENDING".equalsIgnoreCase(record.getStatus())) {
                healthScore -= 5;
            }
        }

        healthScore = Math.max(0, Math.min(100, healthScore));

        String healthStatus;
        if (healthScore >= 80) {
            healthStatus = "Good";
        } else if (healthScore >= 50) {
            healthStatus = "Average";
        } else {
            healthStatus = "Needs Attention";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bikeId", bikeId);
        response.put("healthScore", healthScore);
        response.put("healthStatus", healthStatus);
        response.put("totalMaintenanceRecords", records.size());

        return response;
    }
}