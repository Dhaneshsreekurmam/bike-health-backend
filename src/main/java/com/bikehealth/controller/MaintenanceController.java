package com.bikehealth.controller;

import com.bikehealth.entity.Maintenance;
import com.bikehealth.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<Maintenance> addMaintenance(@RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.addMaintenance(maintenance));
    }

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        return ResponseEntity.ok(maintenanceService.getAllMaintenance());
    }

    @GetMapping("/bike/{bikeId}")
    public ResponseEntity<List<Maintenance>> getMaintenanceByBikeId(@PathVariable Long bikeId) {
        return ResponseEntity.ok(maintenanceService.getMaintenanceByBikeId(bikeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> getMaintenanceById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> updateMaintenance(
            @PathVariable Long id,
            @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.updateMaintenance(id, maintenance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.ok(Map.of("message", "Maintenance record deleted successfully"));
    }
}