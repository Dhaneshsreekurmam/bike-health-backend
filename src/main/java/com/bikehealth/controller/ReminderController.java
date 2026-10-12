
package com.bikehealth.controller;

import com.bikehealth.entity.Maintenance;
import com.bikehealth.repository.MaintenanceRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final MaintenanceRepository maintenanceRepository;

    public ReminderController(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @GetMapping
    public List<Maintenance> getReminders() {
        LocalDate today = LocalDate.now();
        LocalDate upcomingDate = today.plusDays(30);

        return maintenanceRepository.findAll()
                .stream()
                .filter(m -> m.getNextDueDate() != null)
                .filter(m -> !m.getNextDueDate().isAfter(upcomingDate))
                .toList();
    }
}