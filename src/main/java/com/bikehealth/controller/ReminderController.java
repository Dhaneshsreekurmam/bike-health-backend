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
@RequestMapping("/api/reminders")
public class ReminderController {

    private final MaintenanceRepository maintenanceRepository;
    private final BikeRepository bikeRepository;

    public ReminderController(MaintenanceRepository maintenanceRepository, BikeRepository bikeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.bikeRepository = bikeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getReminders(@RequestParam(required = false) Long bikeId) {
        LocalDate today = LocalDate.now();
        LocalDate upcomingHorizon = today.plusDays(45);

        Map<Long, Bike> bikeMap = new HashMap<>();
        bikeRepository.findAll().forEach(b -> bikeMap.put(b.getId(), b));

        List<Maintenance> records = (bikeId != null) 
                ? maintenanceRepository.findByBikeId(bikeId)
                : maintenanceRepository.findAll();

        List<Map<String, Object>> reminders = records.stream()
                .filter(m -> m.getNextDueDate() != null)
                .filter(m -> {
                    boolean isPendingOrProgress = m.getStatus() == null || 
                            "PENDING".equalsIgnoreCase(m.getStatus()) || 
                            "IN_PROGRESS".equalsIgnoreCase(m.getStatus()) ||
                            "OVERDUE".equalsIgnoreCase(m.getStatus());
                    boolean isRelevantDate = !m.getNextDueDate().isAfter(upcomingHorizon);
                    return isPendingOrProgress && isRelevantDate;
                })
                .sorted(Comparator.comparing(Maintenance::getNextDueDate))
                .map(m -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", m.getId());
                    item.put("bikeId", m.getBikeId());
                    item.put("taskName", m.getTaskName());
                    item.put("description", m.getDescription());
                    item.put("serviceDate", m.getServiceDate());
                    item.put("nextDueDate", m.getNextDueDate());
                    item.put("cost", m.getCost());
                    item.put("status", m.getStatus() != null ? m.getStatus() : "PENDING");

                    Bike bike = bikeMap.get(m.getBikeId());
                    if (bike != null) {
                        item.put("bikeName", bike.getName());
                        item.put("bikeBrand", bike.getBrand());
                        item.put("bikeModel", bike.getModel());
                        item.put("currentKm", bike.getCurrentKm());
                    } else {
                        item.put("bikeName", "Unknown Bike");
                        item.put("bikeBrand", "");
                        item.put("bikeModel", "");
                        item.put("currentKm", 0.0);
                    }

                    long daysRemaining = ChronoUnit.DAYS.between(today, m.getNextDueDate());
                    item.put("daysRemaining", daysRemaining);
                    item.put("isOverdue", daysRemaining < 0);

                    return item;
                })
                .toList();

        return ResponseEntity.ok(reminders);
    }
}