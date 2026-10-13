package com.bikehealth.controller;

import com.bikehealth.entity.Bike;
import com.bikehealth.entity.Maintenance;
import com.bikehealth.repository.BikeRepository;
import com.bikehealth.repository.MaintenanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final MaintenanceRepository maintenanceRepository;
    private final BikeRepository bikeRepository;

    public HealthController(MaintenanceRepository maintenanceRepository, BikeRepository bikeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.bikeRepository = bikeRepository;
    }

    @GetMapping("/{bikeId}")
    public ResponseEntity<Map<String, Object>> getHealthScore(@PathVariable Long bikeId) {
        Optional<Bike> bikeOpt = bikeRepository.findById(bikeId);
        if (bikeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Bike bike = bikeOpt.get();
        List<Maintenance> records = maintenanceRepository.findByBikeId(bikeId);

        Map<String, Object> response = calculateHealthMetrics(bike, records);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getHealthOverview() {
        List<Bike> bikes = bikeRepository.findAll();
        List<Maintenance> allRecords = maintenanceRepository.findAll();

        int totalBikes = bikes.size();
        double totalCost = allRecords.stream().mapToDouble(Maintenance::getCost).sum();
        long completedTasks = allRecords.stream().filter(m -> "COMPLETED".equalsIgnoreCase(m.getStatus())).count();
        long pendingTasks = allRecords.stream().filter(m -> "PENDING".equalsIgnoreCase(m.getStatus())).count();

        LocalDate today = LocalDate.now();
        long overdueTasks = allRecords.stream()
                .filter(m -> !"COMPLETED".equalsIgnoreCase(m.getStatus()))
                .filter(m -> m.getNextDueDate() != null && m.getNextDueDate().isBefore(today))
                .count();

        double avgScore = 0;
        List<Map<String, Object>> bikeScores = new ArrayList<>();
        for (Bike bike : bikes) {
            List<Maintenance> bikeRecords = maintenanceRepository.findByBikeId(bike.getId());
            Map<String, Object> metrics = calculateHealthMetrics(bike, bikeRecords);
            avgScore += ((Number) metrics.get("healthScore")).intValue();
            bikeScores.add(metrics);
        }
        if (totalBikes > 0) {
            avgScore = Math.round((avgScore / totalBikes) * 10.0) / 10.0;
        }

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalBikes", totalBikes);
        overview.put("averageFleetScore", avgScore);
        overview.put("totalMaintenanceExpenditure", totalCost);
        overview.put("completedTasks", completedTasks);
        overview.put("pendingTasks", pendingTasks);
        overview.put("overdueTasks", overdueTasks);
        overview.put("bikeHealthSummaries", bikeScores);

        return ResponseEntity.ok(overview);
    }

    private Map<String, Object> calculateHealthMetrics(Bike bike, List<Maintenance> records) {
        LocalDate today = LocalDate.now();
        int healthScore = 100;
        List<String> recommendations = new ArrayList<>();

        int overdueCount = 0;
        int pendingCount = 0;
        int completedCount = 0;
        double totalSpent = 0;
        LocalDate lastServiceDate = null;

        boolean hasEngineService = false;
        boolean hasBrakeService = false;
        boolean hasChainDriveService = false;

        for (Maintenance record : records) {
            totalSpent += record.getCost();

            String status = record.getStatus() != null ? record.getStatus() : "PENDING";
            String taskLower = record.getTaskName() != null ? record.getTaskName().toLowerCase() : "";

            if ("COMPLETED".equalsIgnoreCase(status)) {
                completedCount++;
                if (record.getServiceDate() != null) {
                    if (lastServiceDate == null || record.getServiceDate().isAfter(lastServiceDate)) {
                        lastServiceDate = record.getServiceDate();
                    }
                }
                if (taskLower.contains("oil") || taskLower.contains("engine")) hasEngineService = true;
                if (taskLower.contains("brake")) hasBrakeService = true;
                if (taskLower.contains("chain") || taskLower.contains("drive") || taskLower.contains("tire")) hasChainDriveService = true;
            } else {
                pendingCount++;
                if (record.getNextDueDate() != null && record.getNextDueDate().isBefore(today)) {
                    overdueCount++;
                    healthScore -= 20;
                    long daysLate = ChronoUnit.DAYS.between(record.getNextDueDate(), today);
                    recommendations.add("Overdue (" + daysLate + "d late): " + record.getTaskName());
                } else {
                    healthScore -= 6;
                    recommendations.add("Upcoming scheduled: " + record.getTaskName());
                }
            }
        }

        if (bike.getCurrentKm() > 5000 && completedCount == 0) {
            healthScore -= 25;
            recommendations.add("High mileage (" + bike.getCurrentKm() + " km) without recorded service records.");
        }

        if (lastServiceDate != null) {
            long daysSinceLastService = ChronoUnit.DAYS.between(lastServiceDate, today);
            if (daysSinceLastService > 180) {
                healthScore -= 10;
                recommendations.add("Last completed service was " + daysSinceLastService + " days ago. A general checkup is advised.");
            }
        } else if (records.isEmpty()) {
            recommendations.add("Log initial maintenance (Engine oil & inspection) to establish bike health baseline.");
        }

        healthScore = Math.max(10, Math.min(100, healthScore));

        String healthStatus;
        String statusColor;
        if (healthScore >= 85) {
            healthStatus = "Excellent";
            statusColor = "#10B981";
        } else if (healthScore >= 70) {
            healthStatus = "Good";
            statusColor = "#3B82F6";
        } else if (healthScore >= 50) {
            healthStatus = "Fair - Needs Attention";
            statusColor = "#F59E0B";
        } else {
            healthStatus = "Critical Service Required";
            statusColor = "#EF4444";
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Motorcycle is in prime condition. Keep up regular maintenance checks!");
        }

        int engineHealth = hasEngineService || (records.isEmpty() && bike.getCurrentKm() < 2000) ? 95 : Math.max(40, healthScore - 5);
        int brakeHealth = hasBrakeService || (records.isEmpty() && bike.getCurrentKm() < 3000) ? 92 : Math.max(45, healthScore);
        int driveHealth = hasChainDriveService || (records.isEmpty() && bike.getCurrentKm() < 2500) ? 90 : Math.max(50, healthScore - 8);

        Map<String, Object> response = new HashMap<>();
        response.put("bikeId", bike.getId());
        response.put("bikeName", bike.getName());
        response.put("brand", bike.getBrand());
        response.put("model", bike.getModel());
        response.put("currentKm", bike.getCurrentKm());
        response.put("healthScore", healthScore);
        response.put("healthStatus", healthStatus);
        response.put("statusColor", statusColor);
        response.put("totalMaintenanceRecords", records.size());
        response.put("completedRecords", completedCount);
        response.put("pendingRecords", pendingCount);
        response.put("overdueRecords", overdueCount);
        response.put("totalSpent", totalSpent);
        response.put("lastServiceDate", lastServiceDate);
        response.put("engineHealth", engineHealth);
        response.put("brakeHealth", brakeHealth);
        response.put("driveHealth", driveHealth);
        response.put("recommendations", recommendations);

        return response;
    }
}